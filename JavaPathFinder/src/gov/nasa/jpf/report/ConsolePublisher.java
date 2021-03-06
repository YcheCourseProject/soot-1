//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.Error;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.Path;
import gov.nasa.jpf.jvm.Step;
import gov.nasa.jpf.jvm.Transition;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.Left;
import gov.nasa.jpf.util.RepositoryEntry;
import gov.nasa.jpf.util.Source;

public class ConsolePublisher extends Publisher {

  // output destinations
  String fileName;
  FileOutputStream fos;

  String port;

  // the various degrees of information for program traces
  boolean showCG;
  boolean showSteps;
  boolean showLocation;
  boolean showSource;
  boolean showMethod;
  boolean showCode;

  public ConsolePublisher(Config conf, Reporter reporter) {
    super(conf, reporter);

    // options controlling the output destination
    fileName = conf.getString("report.console.file");
    port = conf.getString("report.console.port");

    // options controlling what info should be included in a trace
    showCG = conf.getBoolean("report.console.show_cg", true);
    showSteps = conf.getBoolean("report.console.show_steps", true);
    showLocation = conf.getBoolean("report.console.show_location", true);
    showSource = conf.getBoolean("report.console.show_source", true);
    showMethod = conf.getBoolean("report.console.show_method", false);
    showCode = conf.getBoolean("report.console.show_code", false);
  }

  public String getName() {
    return "console";
  }

  protected void openChannel(){

    if (fileName != null) {
      try {
        fos = new FileOutputStream(fileName);
        out = new PrintWriter( fos);
      } catch (FileNotFoundException x) {
        // fall back to System.out
      }
    } else if (port != null) {
      // <2do>
    }

    if (out == null){
      out = new PrintWriter(System.out, true);
    }
  }

  protected void closeChannel() {
    if (fos != null){
      out.close();
    }
  }

  public void publishTopicStart (String topic) {
    out.println();
    out.print("====================================================== ");
    out.println(topic);
  }

  public void publishTopicEnd (String topic) {
    // nothing here
  }

  public void publishStart() {
    super.publishStart();

    if (startTopics.length > 0){ // only report if we have output for this phase
      publishTopicStart("search started: " + formatDTG(reporter.getStartDate()));
    }
  }

  public void publishFinished() {
    super.publishFinished();

    if (finishedTopics.length > 0){ // only report if we have output for this phase
      publishTopicStart("search finished: " + formatDTG(reporter.getFinishedDate()));
    }
  }

  protected void publishJPF() {
    out.println(reporter.getJPFBanner());
    out.println();
  }

  protected void publishDTG() {
    out.println("started: " + reporter.getStartDate());
  }

  protected void publishUser() {
    out.println("user: " + reporter.getUser());
  }

  protected void publishJPFConfig() {
    publishTopicStart("JPF configuration");

    TreeMap<Object,Object> map = conf.asOrderedMap();
    Set<Map.Entry<Object,Object>> eSet = map.entrySet();

    for (Object src : conf.getSources()){
      out.print("property source: ");
      out.println(conf.getSourceName(src));
    }    
    
    out.println("properties:");
    for (Map.Entry<Object,Object> e : eSet) {
      out.println("  " + e.getKey() + "=" + e.getValue());
    }
  }

  protected void publishPlatform() {
    publishTopicStart("platform");
    out.println("hostname: " + reporter.getHostName());
    out.println("arch: " + reporter.getArch());
    out.println("os: " + reporter.getOS());
    out.println("java: " + reporter.getJava());
  }


  protected void publishSuT() {
    publishTopicStart("system under test");

    String mainCls = conf.getTarget();
    if (mainCls != null) {
      String mainPath = reporter.getSuT();
      if (mainPath != null) {
        out.println("application: " + mainPath);

        RepositoryEntry rep = RepositoryEntry.getRepositoryEntry(mainPath);
        if (rep != null) {
          out.println("repository: " + rep.getRepository());
          out.println("revision: " + rep.getRevision());
        }
      } else {
        out.println("application: " + mainCls + ".class");
      }
    } else {
      out.println("application: ?");
    }

    String[] args = conf.getTargetArgs();
    if (args.length > 0) {
      out.print("arguments:   ");
      for (String s : args) {
        out.print(s);
        out.print(' ');
      }
      out.println();
    }
  }

  protected void publishError() {
    Error e = reporter.getLastError();

    publishTopicStart("error " + reporter.getLastErrorId());
    out.println(e.getDescription());

    String s = e.getDetails();
    if (s != null) {
      out.println(s);
    }

  }

  protected void publishConstraint() {
    String constraint = reporter.getLastSearchConstraint();
    publishTopicStart("search constraint");
    out.println(constraint);  // not much info here yet
  }

  protected void publishResult() {
    List<Error> errors = reporter.getErrors();

    publishTopicStart("results");

    if (errors.isEmpty()) {
      out.println("no errors detected");
    } else {
      for (Error e : errors) {
        out.print("error #");
        out.print(e.getId());
        out.print(": ");
        out.print(e.getDescription());

        String s = e.getDetails();
        if (s != null) {
          s = s.replace('\n', ' ');
          s = s.replace('\t', ' ');
          s = s.replace('\r', ' ');
          out.print(" \"");
          if (s.length() > 50){
            out.print(s.substring(0,50));
            out.print("...");
          } else {
            out.print(s);
          }
          out.print('"');
        }

        out.println();
      }
    }
  }

  /**
   * this is done as part of the property violation reporting, i.e.
   * we have an error
   */
  protected void publishTrace() {
    Path path = reporter.getPath();
    int i=0;

    if (path.size() == 0) {
      return; // nothing to publish
    }

    publishTopicStart("trace " + reporter.getLastErrorId());

    for (Transition t : path) {
      out.print("------------------------------------------------------ ");
      out.println("transition #" + i++ + " thread: " + t.getThreadIndex());

      if (showCG){
        out.println(t.getChoiceGenerator());
      }

      if (showSteps) {
        String lastLine = null;
        MethodInfo lastMi = null;
        int nNoSrc=0;

        for (Step s : t) {
          if (showSource) {
            String line = s.getLineString();
            if (line != null) {
              if (!line.equals(lastLine)) {
                if (nNoSrc > 0){
                  out.println("      [" + nNoSrc + " insn w/o sources]");
                }

                out.print("  ");
                if (showLocation) {
                  out.print(Left.format(s.getLocationString(),30));
                  out.print(" : ");
                }
                out.println(line.trim());
                nNoSrc = 0;

              }
            } else { // no source
              nNoSrc++;
            }
            
            lastLine = line;
          }

          if (showCode) {
            Instruction insn = s.getInstruction();
            if (showMethod){
              MethodInfo mi = insn.getMethodInfo();
              if (mi != lastMi) {
                ClassInfo mci = mi.getClassInfo();
                out.print("    ");
                if (mci != null) {
                  out.print(mci.getName());
                  out.print(".");
                }
                out.println(mi.getUniqueName());
                lastMi = mi;
              }
            }
            out.print("      ");
            out.println(insn);
          }
        }

        if (showSource && !showCode && (nNoSrc > 0)) {
          out.println("      [" + nNoSrc + " insn w/o sources]");
        }
      }
    }
  }

  protected void publishOutput() {
    Path path = reporter.getPath();

    if (path.size() == 0) {
      return; // nothing to publish
    }

    publishTopicStart("output " + reporter.getLastErrorId());

    if (path.hasOutput()) {
      for (Transition t : path) {
        String s = t.getOutput();
        if (s != null){
          out.print(s);
        }
      }
    } else {
      out.println("no output");
    }
  }

  protected void publishSnapshot() {
    JVM vm = reporter.getVM();

    // not so nice - we have to delegate this since it's using a lot of internals, and is also
    // used in debugging
    publishTopicStart("snapshot " + reporter.getLastErrorId());

    if (vm.getPathLength() > 0) {
      vm.printLiveThreadStatus(out);
    } else {
      out.println("initial program state");
    }
  }

  protected void publishStatistics() {
    Statistics stat = reporter.getStatistics();
    publishTopicStart("statistics");
    out.println("elapsed time:       " + formatHMS(reporter.getElapsedTime()));
    out.println("states:             new=" + stat.newStates + ", visited=" + stat.visitedStates
                                     + ", backtracked=" + stat.backtracked + ", end=" + stat.endStates);
    out.println("search:             maxDepth=" + stat.maxDepth + ", constraints=" + stat.constraints);
    out.println("choice generators:  thread=" + stat.threadCGs + ", data=" + stat.dataCGs);
    out.println("heap:               gc=" + stat.gcCycles + ", new=" + stat.nObjects +
                                                         ", free=" + stat.nRecycled);
    out.println("instructions:       " + stat.insns);
    out.println("max memory:         " + (stat.maxUsed >>20) + "MB");

    out.println("loaded code:        classes=" + ClassInfo.getNumberOfLoadedClasses() + ", methods=" +
                                      MethodInfo.getNumberOfLoadedMethods());
  }

}

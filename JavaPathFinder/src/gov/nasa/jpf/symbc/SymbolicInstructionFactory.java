//
// Copyright (C) 2007 United States Government as represented by the
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

package gov.nasa.jpf.symbc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.jvm.DefaultInstructionFactory;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.util.InstructionFactoryFilter;

/*
 * Refactored version to use the DefaultInstructionFactory -- neha
 */
public class SymbolicInstructionFactory extends DefaultInstructionFactory {
	 static Class<? extends Instruction>[] insnClass;

	  static {
	    insnClass = createInsnClassArray(260); 

	    insnClass[ALOAD_0]         = gov.nasa.jpf.symbc.bytecode.ALOAD.class;
	    insnClass[ALOAD_1]         = gov.nasa.jpf.symbc.bytecode.ALOAD.class;
	    insnClass[ALOAD_2]         = gov.nasa.jpf.symbc.bytecode.ALOAD.class;
	    insnClass[ALOAD_3]         = gov.nasa.jpf.symbc.bytecode.ALOAD.class;
	    insnClass[IADD] = gov.nasa.jpf.symbc.bytecode.IADD.class;
	    insnClass[IAND] = gov.nasa.jpf.symbc.bytecode.IAND.class;
	    insnClass[IINC] = gov.nasa.jpf.symbc.bytecode.IINC.class;
	    insnClass[ISUB] = gov.nasa.jpf.symbc.bytecode.ISUB.class;
	    insnClass[IMUL] = gov.nasa.jpf.symbc.bytecode.IMUL.class;
	    insnClass[INEG] = gov.nasa.jpf.symbc.bytecode.INEG.class;
	    insnClass[IFLE] = gov.nasa.jpf.symbc.bytecode.IFLE.class;
	    insnClass[IFLT] = gov.nasa.jpf.symbc.bytecode.IFLT.class;
	    insnClass[IFGE] = gov.nasa.jpf.symbc.bytecode.IFGE.class;
	    insnClass[IFGT] = gov.nasa.jpf.symbc.bytecode.IFGT.class;
	    insnClass[IFEQ] = gov.nasa.jpf.symbc.bytecode.IFEQ.class;
	    insnClass[IFNE] = gov.nasa.jpf.symbc.bytecode.IFNE.class;
	    insnClass[INVOKESTATIC] = gov.nasa.jpf.symbc.bytecode.INVOKESTATIC.class;
	    insnClass[INVOKEVIRTUAL] = gov.nasa.jpf.symbc.bytecode.INVOKEVIRTUAL.class;
	    insnClass[IF_ICMPGE] = gov.nasa.jpf.symbc.bytecode.IF_ICMPGE.class;
	    insnClass[IF_ICMPGT] = gov.nasa.jpf.symbc.bytecode.IF_ICMPGT.class;
	    insnClass[IF_ICMPLE] = gov.nasa.jpf.symbc.bytecode.IF_ICMPLE.class;
	    insnClass[IF_ICMPLT] = gov.nasa.jpf.symbc.bytecode.IF_ICMPLT.class;
	    insnClass[IDIV] = gov.nasa.jpf.symbc.bytecode.IDIV.class;
	    insnClass[IXOR] = gov.nasa.jpf.symbc.bytecode.IXOR.class;
	    insnClass[IOR] = gov.nasa.jpf.symbc.bytecode.IOR.class;
	    insnClass[IREM] = gov.nasa.jpf.symbc.bytecode.IREM.class;
	    insnClass[IF_ICMPEQ] = gov.nasa.jpf.symbc.bytecode.IF_ICMPEQ.class;
	    insnClass[IF_ICMPNE] = gov.nasa.jpf.symbc.bytecode.IF_ICMPNE.class;
	    insnClass[INVOKESPECIAL] = gov.nasa.jpf.symbc.bytecode.INVOKESPECIAL.class;
	    insnClass[FADD] = gov.nasa.jpf.symbc.bytecode.FADD.class;
	    insnClass[FDIV] = gov.nasa.jpf.symbc.bytecode.FDIV.class;
	    insnClass[FMUL] = gov.nasa.jpf.symbc.bytecode.FMUL.class;
	    insnClass[FNEG] = gov.nasa.jpf.symbc.bytecode.FNEG.class;
	    insnClass[FREM] = gov.nasa.jpf.symbc.bytecode.FREM.class;
	    insnClass[FSUB] = gov.nasa.jpf.symbc.bytecode.FSUB.class;
	    insnClass[FCMPG] = gov.nasa.jpf.symbc.bytecode.FCMPG.class;
	    insnClass[FCMPL] = gov.nasa.jpf.symbc.bytecode.FCMPL.class;
	    insnClass[DADD] = gov.nasa.jpf.symbc.bytecode.DADD.class;
	    insnClass[DCMPG] = gov.nasa.jpf.symbc.bytecode.DCMPG.class;
	    insnClass[DCMPL] = gov.nasa.jpf.symbc.bytecode.DCMPL.class;
	    insnClass[DDIV] = gov.nasa.jpf.symbc.bytecode.DDIV.class;
	    insnClass[DMUL] = gov.nasa.jpf.symbc.bytecode.DMUL.class;
	    insnClass[DNEG] = gov.nasa.jpf.symbc.bytecode.DNEG.class;
	    insnClass[DREM] = gov.nasa.jpf.symbc.bytecode.DREM.class;
	    insnClass[DSUB] = gov.nasa.jpf.symbc.bytecode.DSUB.class;
	    insnClass[LADD] = gov.nasa.jpf.symbc.bytecode.LADD.class;
	    insnClass[LAND] = gov.nasa.jpf.symbc.bytecode.LAND.class;
	    insnClass[LCMP] = gov.nasa.jpf.symbc.bytecode.LCMP.class;
	    insnClass[LDIV] = gov.nasa.jpf.symbc.bytecode.LDIV.class;
	    insnClass[LMUL] = gov.nasa.jpf.symbc.bytecode.LMUL.class;
	    insnClass[LNEG] = gov.nasa.jpf.symbc.bytecode.LNEG.class;
	    insnClass[LOR] = gov.nasa.jpf.symbc.bytecode.LOR.class;
	    insnClass[LREM] = gov.nasa.jpf.symbc.bytecode.LREM.class;
	    insnClass[LSHL] = gov.nasa.jpf.symbc.bytecode.LSHL.class;
	    insnClass[LSHR] = gov.nasa.jpf.symbc.bytecode.LSHR.class;
	    insnClass[LSUB] = gov.nasa.jpf.symbc.bytecode.LSUB.class;
	    insnClass[LUSHR] = gov.nasa.jpf.symbc.bytecode.LUSHR.class;
	    insnClass[LXOR] = gov.nasa.jpf.symbc.bytecode.LXOR.class;
		insnClass[I2D] = gov.nasa.jpf.symbc.bytecode.I2D.class;
		insnClass[D2I] = gov.nasa.jpf.symbc.bytecode.D2I.class;
		insnClass[D2L] = gov.nasa.jpf.symbc.bytecode.D2L.class;
		insnClass[I2F] = gov.nasa.jpf.symbc.bytecode.I2F.class;
		insnClass[L2D] = gov.nasa.jpf.symbc.bytecode.L2D.class;
		insnClass[L2F] = gov.nasa.jpf.symbc.bytecode.L2F.class;
		insnClass[F2L] = gov.nasa.jpf.symbc.bytecode.F2L.class;
		insnClass[F2I] = gov.nasa.jpf.symbc.bytecode.F2I.class;
		insnClass[LOOKUPSWITCH] = gov.nasa.jpf.symbc.bytecode.LOOKUPSWITCH.class;
		insnClass[TABLESWITCH] = gov.nasa.jpf.symbc.bytecode.TABLESWITCH.class;
		insnClass[D2F] = gov.nasa.jpf.symbc.bytecode.D2F.class;
		insnClass[F2D] = gov.nasa.jpf.symbc.bytecode.F2D.class;
		insnClass[I2B] = gov.nasa.jpf.symbc.bytecode.I2B.class;
		insnClass[I2C] = gov.nasa.jpf.symbc.bytecode.I2C.class;
		insnClass[I2S] = gov.nasa.jpf.symbc.bytecode.I2S.class;
		insnClass[I2L] = gov.nasa.jpf.symbc.bytecode.I2L.class;
		insnClass[L2I] = gov.nasa.jpf.symbc.bytecode.IADD.class;
		insnClass[GETFIELD] = gov.nasa.jpf.symbc.bytecode.GETFIELD.class;
		insnClass[GETSTATIC] = gov.nasa.jpf.symbc.bytecode.GETSTATIC.class;
		//TODO: to review
        //From Fujitsu:
		insnClass[NEW] = gov.nasa.jpf.symbc.bytecode.NEW.class;
		insnClass[IFNULL] = gov.nasa.jpf.symbc.bytecode.IFNULL.class;
		insnClass[IFNONNULL] = gov.nasa.jpf.symbc.bytecode.IFNONNULL.class;
		// IMPORTANT: if any new bytecodes are added make sure to update the 
		// length of the array which is at the top of the function
	  };
	  
	static public String[] dp;

	//bytecodes replaced by our symbolic implementation
	/** This is not needed anymore with the new implementation --neha
	    static final String[] BC_NAMES = {
		"IADD", "IAND", "IINC", "ISUB","IMUL","INEG",
		"IFLE","IFLT","IFGE","IFGT","IFEQ","IFNE",
		"INVOKESTATIC","INVOKEVIRTUAL",
		"IF_ICMPGE","IF_ICMPGT","IF_ICMPLE","IF_ICMPLT",
		"IDIV", "IXOR", "IOR", "IREM", "IF_ICMPEQ", "IF_ICMPNE","INVOKESPECIAL",
		"FADD", "FDIV", "FMUL", "FNEG","FREM", "FSUB", "FCMPG", "FCMPL",
        "DADD", "DCMPG", "DCMPL", "DDIV", "DMUL", "DNEG", "DREM", "DSUB",
        "LADD", "LAND", "LCMP", "LDIV", "LMUL", "LNEG", "LOR", "LREM",
        "LSHL", "LSHR", "LSUB", "LUSHR", "LXOR",
        "I2D" , "D2I" , "D2L", "I2F" , "L2D", "L2F" , "F2L" , "F2I",
        "LOOKUPSWITCH", "TABLESWITCH",
        "D2F", "F2D", "I2B", "I2C", "I2S", "I2L", "L2I"
        , "GETFIELD", "GETSTATIC"
        //TODO: to review
        //From Fujitsu:
        , "NEW", "IFNULL", "IFNONNULL"
	};**/

	
	InstructionFactoryFilter filter = new InstructionFactoryFilter(null, new String[] {"java.*", "javax.*" },
			null, null);


	public  SymbolicInstructionFactory (Config conf){
		if (dp==null) {
			dp = conf.getStringArray("symbolic.dp");
			if (dp == null) {
				dp = new String[1];
				dp[0] = "choco";
			}
			System.out.println("symbolic.dp="+dp[0]);
		}


		System.out.println("Symbolic Execution Mode");

	}
	
	public Instruction create(ClassInfo ciMth, int opCode) {

	    if (opCode < insnClass.length){
	      Class<?> cls = insnClass[opCode];
	      if (cls != null && filter.isInstrumentedClass(ciMth)) {
	        try {
	          Instruction insn = (Instruction) cls.newInstance();
	          return insn;

	        } catch (Throwable e) {
	          throw new JPFException("creation of symbc Instruction object for opCode "
	                  + opCode + " failed: " + e);
	        }
	      }
	    }

	    // use default instruction classes
	    return super.create(ciMth, opCode);
	  }
}

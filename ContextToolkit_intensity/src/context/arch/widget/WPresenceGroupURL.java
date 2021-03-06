package context.arch.widget;

import context.arch.comm.DataObject;
import context.arch.generator.IButtonData;
import context.arch.interpreter.IIButton2GroupURL;
import context.arch.storage.Attributes;
import context.arch.storage.Attribute;
import context.arch.storage.AttributeNameValues;
import context.arch.subscriber.Callbacks;

/**
 * This class is a context widget that provides information on 
 * the preferences of a person whose presence has been detected
 * at a specified location.  In particular, this information is
 * the user id (IButton id), location, and group url.
 * It has the following callbacks: UPDATE. It supports polling and
 * subscriptions.  It subscribes to a WPersonPresence at the
 * the specified location and uses the IIButton2GroupURL.
 *
 * @see context.arch.widget.Widget
 * @see context.arch.widget.WPersonPresence
 * @see context.arch.interpreter.IIButton2GroupURL
 */
public class WPresenceGroupURL extends WPersonPresence {

  /**
   * Debug flag. Set to true to see debug messages.
   */
  private static final boolean DEBUG = false;

  /**
   * Widget version number
   */
  public String VERSION_NUMBER = "1.0.0";

  /**
   * Name of widget
   */
  public static final String CLASSNAME = "PresenceGroupURL";

  /**
   * Tag for url of user's homepage
   */
  public static final String URL = IIButton2GroupURL.URL;

  /**
   * Default port to run this widget on is 5600
   */
  public static final int DEFAULT_PORT = 5600;

  private String location;
  private String iHost;
  private int iPort;

  /**
   * Constructor that creates the widget at the given location and
   * monitors communications on the DEFAULT_PORT. 
   * It sets the id of this widget to CLASSNAME_<location value> and enables storage.
   *  
   * @param location Location the widget is "monitoring"
   * @param ihost Name of the machine the IButton2GroupURL interpreter is running on
   * @param iport Port number the IButton2GroupURL interpreter is running on
   *
   * @see context.arch.widget.WPersonPresence
   */
  public WPresenceGroupURL(String location, String ihost, int iport) {
    this(location,DEFAULT_PORT,ihost,iport,true);
  }

  /**
   * Constructor that creates the widget at the given location and
   * monitors communications on the DEFAULT_PORT.  
   * It sets the id of this widget to CLASSNAME_<location value> and sets storage
   * to storageFlag.
   *  
   * @param location Location the widget is "monitoring"
   * @param ihost Name of the machine the IButton2Group interpreter is running on
   * @param iport Port number the IButton2Group interpreter is running on
   * @param storageFlag Flag to set storage functionality to
   *
   * @see context.arch.widget.WPersonPresence
   */
  public WPresenceGroupURL(String location, String ihost, int iport, boolean storageFlag) {
    this(location,DEFAULT_PORT,ihost,iport,storageFlag);
  }

  /**
   * Constructor that creates the widget at the given location and
   * monitors communications on the given port.  
   * It sets the id of this widget to CLASSNAME_<location value> and enables storage.
   *  
   * @param location Location the widget is "monitoring"
   * @param localport Port number this widget is running on
   * @param ihost Name of the machine the IIButton2Group is running on
   * @param iport Port number the IIButton2Group is running on
   *
   * @see context.arch.widget.WPersonPresence
   */
  public WPresenceGroupURL(String location, int localport, String ihost, int iport) {
    this(location, localport, ihost, iport, true);
  }

  /**
   * Constructor that creates the widget at the given location and
   * monitors communications on the given port.
   * It sets the id of this widget to CLASSNAME_<location value> and sets
   * storage functionality to storageFlag.
   *  
   * @param location Location the widget is "monitoring"
   * @param localport Port number this widget is running on
   * @param ihost Name of the machine the IIButton2GroupURL is running on
   * @param iport Port number the IIButton2GroupURL is running on
   * @param storageFlag Flag to set storage functionality to
   *
   * @see context.arch.widget.WPersonPresence
   */
  public WPresenceGroupURL(String location, int localport, String ihost, int iport, boolean storageFlag) {
    super(location,localport, CLASSNAME+SPACER+location, storageFlag);
    iHost = ihost;
    iPort = iport;
    this.location = location;
  }

  /**
   * This method implements the abstract method Widget.setAttributes().
   * It defines the attributes for the widget as:
   *    TIMESTAMP, USERID, LOCATION, and URL
   *
   * @return the Attributes used by this widget
   */
  protected Attributes setAttributes() {
    Attributes atts = super.setAttributes();//Here it sets TIMESTAMP, USERID and LOCATION 
    atts.addAttribute(URL); //Here it sets URL
    return atts;
  }
  
  /**
   * This method implements the abstract method Widget.setCallbacks().
   * It defines the callbacks for the widget as:
   *    UPDATE with the attributes TIMESTAMP, USERID, LOCATION, URL
   *
   * @return the Callbacks used by this widget
   */
  protected Callbacks setCallbacks() {
    Callbacks calls = new Callbacks();
    calls.addCallback(UPDATE,setAttributes());
    return calls;
  }

  /**
   * This method converts the IButtonData object to an AttributeNameValues
   * object.  It overrides the method in WPersonPresence, basically
   * doing the same thing, except it also uses an interpreter to 
   * convert the USERID to a URL
   *
   * @param data IButtonData object to be converted
   * @return AttributeNameValues object containing the data in the IButtonData object
   */
  protected AttributeNameValues IButtonData2Attributes(IButtonData data) {
    AttributeNameValues atts = new AttributeNameValues();
    DataObject urlObject = askIButton2GroupURL(data.getId());
    if (urlObject == null) {
      return null;
    }

    AttributeNameValues anvs = new AttributeNameValues(urlObject);
    String url = (String)anvs.getAttributeNameValue(URL).getValue();
    if (url == null) {
      return null;
    }

    atts.addAttributeNameValue(USERID,data.getId());
    atts.addAttributeNameValue(LOCATION,location);
    atts.addAttributeNameValue(TIMESTAMP,data.getTimestamp(),Attribute.LONG);
    atts.addAttributeNameValue(URL,url);
    return atts;
  }

  /**
   * This private method asks the IIButton2GroupURL for a  
   * group for the given iButton id.
   *
   * @param userid iButton id to get the url for
   * @return DataObject containing the results of the interpretation
   */
  private DataObject askIButton2GroupURL(String userid) {
    AttributeNameValues data = new AttributeNameValues();
    data.addAttributeNameValue(IIButton2GroupURL.IBUTTONID, userid);
    return askInterpreter(iHost,iPort,IIButton2GroupURL.CLASSNAME,data);
  }
	

  /**
   * Temporary main method to create a widget with id and port and connection to
   * other low level widgets specified by command line arguments
   */
  public static void main(String argv[]) {
    if (argv.length == 3) {
      if (DEBUG) {
        System.out.println("Attempting to create a WPresenceGroupURL on "+DEFAULT_PORT+" at "+argv[0]+" with storage enabled");
      }
      WPresenceGroupURL wpg = new WPresenceGroupURL(argv[0], argv[1], Integer.parseInt(argv[2]));
    }
    else if (argv.length == 4) {
      if ((argv[3].equals("false")) || (argv[3].equals("true"))) {
        if (DEBUG) {
          System.out.println("Attempting to create a WPresenceGroupURL on "+argv[1]+" for user "+argv[0]+" with storage set to" +argv[3]);
        }
        WPresenceGroupURL wpg = new WPresenceGroupURL(argv[0], argv[1], Integer.parseInt(argv[2]), Boolean.valueOf(argv[3]).booleanValue());
      }
      else {
        if (DEBUG) {
          System.out.println("Attempting to create a WPresenceGroupURL on "+argv[1]+" for user "+argv[0]+" with storage enabled");
        }
        WPresenceGroupURL wpg = new WPresenceGroupURL(argv[0], Integer.parseInt(argv[1]), argv[2], Integer.parseInt(argv[3]));
      }
    }
    else if (argv.length == 5) {
      if (DEBUG) {
        System.out.println("Attempting to create a WPresenceGroupURL on "+argv[1]+" for user "+argv[0] +" with storage set to "+argv[4]);
      }
      WPresenceGroupURL wpg = new WPresenceGroupURL(argv[0], Integer.parseInt(argv[1]), argv[2], Integer.parseInt(argv[3]), Boolean.valueOf(argv[4]).booleanValue());
    }
    else {
      System.out.println("USAGE: java context.arch.widget.WPresenceGroupURL <location> [port] <IIButton2GroupHost/ip> <IIButton2GroupPort> [storageflag]");
    }
  }

}


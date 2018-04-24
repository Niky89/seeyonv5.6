//package updateDog;
//
//import java.awt.Dimension;
//import java.awt.EventQueue;
//import java.awt.Toolkit;
//import updateDog.ui.UpdateDog;
//
//public class RunApps
//{
//  public static final int toolsType = 1;
//  private static final String installPath = "";
//  public static final String splitStr = "@";
//  private static final String publicKeyPath = "windows.dat";
//  private static final String messagePath = "message.dat";
//  private static final String officeKeyFile = "officeplugin.office";
//  private static final String checkFilePath = "checkFile.properties";
//  
//  public static void main(String[] args)
//  {
//    EventQueue.invokeLater(new Runnable()
//    {
//      public void run()
//      {
//        try
//        {
//          UpdateDog frmv = new UpdateDog();
//          int width = frmv.getWidth();
//          int height = frmv.getHeight();
//          int w = (Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2;
//          int h = (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2;
//          frmv.setLocation(w, h);
//          frmv.setDefaultCloseOperation(3);
//          frmv.setVisible(true);
//        }
//        catch (Exception e)
//        {
//          e.printStackTrace();
//        }
//      }
//    });
//  }
//  
//  public static String getMsgPath()
//  {
//    return "message.dat";
//  }
//  
//  public static String getOfficeFile()
//  {
//    return "officeplugin.office";
//  }
//  
//  public static String getInstallPath()
//  {
//    return "";
//  }
//  
//  public static String getCheckFilePath()
//  {
//    return "checkFile.properties";
//  }
//}

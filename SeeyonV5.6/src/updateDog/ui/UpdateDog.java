//package updateDog.ui;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JFileChooser;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//import javax.swing.filechooser.FileFilter;
//
//import updateDog.function.Button11Function;
//import updateDog.function.Button12Function;
//import updateDog.function.Button13Function;
//import updateDog.function.Button14Function;
//import updateDog.function.Button15Function;
//import updateDog.function.Button21Function;
//import updateDog.function.Button31Function;
//import updateDog.function.Button32Function;
//import updateDog.function.Button33Function;
//import updateDog.function.Button34Function;
//import updateDog.function.Button35Function;
//import updateDog.function.Button36Function;
//import www.seeyon.com.mocnoyees.CHKUMocnoyees;
//import www.seeyon.com.mocnoyees.DogException;
//import www.seeyon.com.mocnoyees.Enums;
//import www.seeyon.com.mocnoyees.LRWMMocnoyees;
//
//public class UpdateDog
//  extends JFrame
//{
//  private static final long serialVersionUID = -1376940561964898712L;
//  private static final String versionNo = "v2.0";
//  private JComboBox productLine;
//  private JTextArea textArea;
//  private UpdateDog thisObj;
//  private JComboBox dogType;
//  private JTextField checkFilePath;
//  
//  public UpdateDog()
//  {
//    initialize();
//    this.thisObj = this;
//  }
//  
//  public String getSelected()
//  {
//    String str = (String)this.productLine.getSelectedItem();
//    return str;
//  }
//  
//  public String getDogType()
//  {
//    String str = (String)this.dogType.getSelectedItem();
//    return str;
//  }
//  
//  public String getCheckFileConfig()
//  {
//    String str = this.checkFilePath.getText();
//    return str;
//  }
//  
//  public void alert(String s)
//  {
//    System.out.println(s);
//    this.textArea.append(s);
//    JOptionPane.showMessageDialog(this.thisObj, s);
//  }
//  
//  public void showMsg(String s)
//  {
//    this.textArea.append(s + "\n");
//  }
//  
//  public String getMsg()
//  {
//    return this.textArea.getText();
//  }
//  
//  private void initialize()
//  {
//    setTitle("111");
//    setResizable(false);
//    setBounds(100, 100, 733, 389);
//    setDefaultCloseOperation(3);
//    getContentPane().setLayout(null);
//    
//    JButton button11 = new JButton("注册与更新");
//    button11.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        Button11Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button11.setBounds(53, 57, 141, 23);
//    
//    JButton button13 = new JButton("查看license信息");
//    button13.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        Button13Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button13.setBounds(53, 123, 141, 23);
//    
//    JButton button14 = new JButton("查看硬件ID");
//    button14.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        Button14Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button14.setBounds(53, 156, 141, 23);
//    
//    JButton button12 = new JButton("下载OFFICE认证");
//    button12.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        Button12Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button12.setBounds(53, 90, 141, 23);
//    
//    JButton button16 = new JButton("绑定数据库");
//    button16.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("开始数据库绑定.....");
//        Button11Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button16.setBounds(53, 222, 141, 23);
//    
//    JButton button21 = new JButton("文件签名");
//    button21.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        if ((UpdateDog.this.checkFilePath.getText() == null) || (UpdateDog.this.checkFilePath.getText().length() == 0) || (!new File(UpdateDog.this.checkFilePath.getText()).exists()))
//        {
//          UpdateDog.this.showMsg("安装路径不能空，且必须存在！");
//          return;
//        }
//        Button21Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button21.setBounds(132, 267, 87, 23);
//    
//    JButton button22 = new JButton("验证签名");
//    button22.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        if ((UpdateDog.this.checkFilePath.getText() == null) || (UpdateDog.this.checkFilePath.getText().length() == 0) || (!new File(UpdateDog.this.checkFilePath.getText()).exists()))
//        {
//          UpdateDog.this.showMsg("安装路径不能空，且必须存在！");
//          return;
//        }
//        boolean b = false;
//        try
//        {
//          b = CHKUMocnoyees.checkFile(UpdateDog.this.getFile(UpdateDog.this.thisObj).getAbsolutePath(), UpdateDog.this.checkFilePath.getText());
//          UpdateDog.this.showMsg("验证结果：" + b);
//        }
//        catch (DogException dogE)
//        {
//          UpdateDog.this.showMsg("验证结果：" + dogE.getErrorMsg());
//        }
//      }
//    });
//    button22.setBounds(609, 269, 93, 23);
//    
//    JButton button31 = new JButton("初始化狗");
//    button31.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        Button31Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button31.setBounds(133, 302, 87, 23);
//    
//    JButton button32 = new JButton("狗原始信息");
//    button32.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        Button32Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button32.setBounds(230, 302, 110, 23);
//    
//    JButton button15 = new JButton("读取狗号");
//    button15.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        UpdateDog.this.textArea.setText("");
//        Button15Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button15.setBounds(53, 189, 141, 23);
//    
//    JScrollPane scrollPane = new JScrollPane();
//    scrollPane.setBounds(229, 19, 473, 239);
//    getContentPane().add(scrollPane);
//    
//    this.textArea = new JTextArea();
//    scrollPane.setViewportView(this.textArea);
//    
//    JLabel label = new JLabel("开发者工具:");
//    label.setHorizontalAlignment(4);
//    label.setBounds(53, 273, 70, 15);
//    
//    JLabel lblNewLabel = new JLabel("高级功能:");
//    lblNewLabel.setHorizontalAlignment(4);
//    lblNewLabel.setBounds(53, 306, 70, 15);
//    
//    JButton button33 = new JButton("写入信息");
//    button33.setBounds(350, 302, 87, 23);
//    
//    
//    button33.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        Button33Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    
//    JButton button34 = new JButton("解密信息");
//    button34.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        Button34Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button34.setBounds(447, 302, 87, 23);
//    
//    JButton button35 = new JButton("加密版本文件");
//    button35.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        Button35Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button35.setBounds(133, 335, 121, 23);
//    
//    JButton button36 = new JButton("解密版本文件");
//    button36.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent arg0)
//      {
//        Button36Function.doIt(UpdateDog.this.thisObj);
//      }
//    });
//    button36.setBounds(274, 335, 121, 23);
//    
//    this.productLine = new JComboBox();
//    this.productLine.setBounds(53, 19, 60, 21);
//    Enums.VendorID[] arrayOfVendorID;
//    int j = (arrayOfVendorID = Enums.VendorID.values()).length;
//    for (int i = 0; i < j; i++)
//    {
//      Enums.VendorID e = arrayOfVendorID[i];
//      if (!e.getKey().equals(Enums.VendorID.VendorID_DEFAULT.getKey())) {
//        this.productLine.addItem(e.getKey());
//      }
//    }
//    getContentPane().add(this.productLine);
//    
//    this.dogType = new JComboBox();
//    this.dogType.setBounds(123, 19, 71, 21);
//    this.dogType.addItem("1加密狗");
//    this.dogType.addItem("2软加密文件 ");
//    getContentPane().add(this.dogType);
//    
//    this.checkFilePath = new JTextField();
//    this.checkFilePath.setBounds(311, 268, 288, 21);
//    this.checkFilePath.setColumns(10);
//    
//    JLabel label_1 = new JLabel("产品安装路径");
//    label_1.setBounds(229, 271, 96, 15);
//    
//    getContentPane().add(button11);
//    getContentPane().add(button12);
//    getContentPane().add(button13);
//    getContentPane().add(button14);
//    getContentPane().add(button15);
//    getContentPane().add(button16);
//    getContentPane().add(button31);
//    getContentPane().add(button32);
//    getContentPane().add(button33);
//    getContentPane().add(button34);
//    getContentPane().add(button35);
//    getContentPane().add(button36);
//  }
//  
//  public LRWMMocnoyees getLicense()
//  {
//    String s = getSelected();
//    String dogType = getDogType().substring(0, 1);
//    String dogNo = null;
//    LRWMMocnoyees dog = null;
//    try
//    {
//      if (dogType.equals("1"))
//      {
//        dog = new LRWMMocnoyees(s);
//      }
//      else if (dogType.equals("2"))
//      {
//        File softFile = getFile(this);
//        if ((softFile == null) || (!softFile.exists())) {
//          return null;
//        }
//        dog = new LRWMMocnoyees(softFile);
//      }
//      dogNo = dog.lrwmmocnoyeesa();
//    }
//    catch (DogException e)
//    {
//      showMsg(Enums.ErrorCode.getDisplayName(e.getMessage()));
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//      showMsg(e.getMessage());
//    }
//    return dog;
//  }
//  
//  public File getFile(UpdateDog parent)
//  {
//    JFileChooser fc = new JFileChooser();
//    fc.setCurrentDirectory(new File("."));
//    fc.setDialogTitle("选择需要注册的软加密文件");
//    fc.setMultiSelectionEnabled(false);
//    fc.setFileSelectionMode(0);
//    fc.setFileFilter(new FileFilter()
//    {
//      public boolean accept(File f)
//      {
//        if ((f.getName().endsWith(".seeyonkey")) || (f.isDirectory())) {
//          return true;
//        }
//        return false;
//      }
//      
//      public String getDescription()
//      {
//        return "软加密文件(*.seeyonkey)";
//      }
//    });
//    int result = fc.showOpenDialog(parent);
//    File softFile = null;
//    if (result == 0)
//    {
//      softFile = fc.getSelectedFile();
//      fc.setVisible(false);
//    }
//    return softFile;
//  }
//}

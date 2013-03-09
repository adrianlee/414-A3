import java.io.*;
import java.net.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.*;
import javax.xml.parsers.*;
import java.net.*;

public class MyNode {
  String name;
  Object obj;

  public MyNode(String name, Object obj) {

    this.name = name;
    this.obj = obj;

  }

  public Node getNode() {
    return (Node)this.obj;
  }

  public NodeList getList() {
    return (NodeList)this.obj;
  }
}
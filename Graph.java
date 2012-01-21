import java.util.*;
import java.awt.*;
import java.applet.Applet;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;


class Node
{
    double x;
    double y;

    double dx;
    double dy;

    boolean fixed;
    boolean visible;
    boolean isLink;
    boolean fadeIn;
    boolean fadeOut;

    String nodeName;
    String url;

    Color color;
    int stepCount = 0;
    double currentR;
    double currentG;
    double currentB;
    double fontSize;
    boolean isParent;

}


class Edge
{
    int from;
    int to;
    double len;
}


class GraphPanel extends Panel implements Runnable, MouseListener, MouseMotionListener
{
    Graph graph;
    int nodeCount;
    Node nodes[] = new Node[100];

    int edgeCount;
    Edge edges[] = new Edge[200];
    Node centerNode = null;
    Node OldCenterNode =  new Node();
    Node NewCenterNode = new Node();

    static int count;
    static String node_code[][] = new String[100][100];

    Thread relaxer;

    //Constructor
    GraphPanel(Graph graph)
    {
        this.graph = graph;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public int findNode(String nodeName)
    {
        for (int i = 0 ; i < nodeCount ; i++)
        {
            if (nodes[i].nodeName.equals(nodeName))
            {
                return i;
            }
        }
        return addNode(nodeName);

    }//end findNode

    int addNode(String nodeName)
    {
        Node n = new Node();
        n.x = 10 + 380*Math.random();
        n.y = 10 + 380*Math.random();
        n.nodeName = nodeName;
        nodes[nodeCount] = n;

        return nodeCount++;

    }//end addNode

    void addEdge(String from, String to, int len)
    {
        Edge e = new Edge();
        e.from = findNode(from);
        e.to = findNode(to);
        e.len = len;
        edges[edgeCount++] = e;

    }//end addEdge

    public void run()
    {
        defineNode();
        Thread me = Thread.currentThread();
        while (relaxer == me)
        {
            relax();

            if (Math.random() < 0.03)
            {
                Node n = nodes[(int)(Math.random() * nodeCount)];
                if (!n.fixed)
                {
                    n.x += 100*Math.random() - 50;
                    n.y += 100*Math.random() - 50;
                }
            }

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                break;
            }
         }
    }//end run

    synchronized void relax()
    {
        manageVisible("");
        for (int i = 0 ; i < edgeCount ; i++)
        {
            Edge e3 = edges[i];
            if((nodes[e3.from].visible == true) && (nodes[e3.to].visible == true))
            {
                Edge e = edges[i];
                double vx = nodes[e.to].x - nodes[e.from].x;
                double vy = nodes[e.to].y - nodes[e.from].y;
                double len = Math.sqrt(vx * vx + vy * vy);
                len = (len == 0) ? .0001 : len;
                double f = (edges[i].len - len) / (len * 3);
                double dx = f * vx;
                double dy = f * vy;

                nodes[e.to].dx += dx;
                nodes[e.to].dy += dy;
                nodes[e.from].dx += -dx;
                nodes[e.from].dy += -dy;
            }
        }

        for (int i = 0 ; i < nodeCount ; i++)
        {
            if(nodes[i].visible == true)
            {
                Node n1 = nodes[i];
                double dx = 0;
                double dy = 0;

                for (int j = 0; j < nodeCount; j++)
                {
                    if (nodes[j].visible == true)
                    {
                        if (i == j)
                        {
                            continue;
                        }
                        Node n2 = nodes[j];
                        double vx = n1.x - n2.x;
                        double vy = n1.y - n2.y;
                        double len = vx * vx + vy * vy;
                        if (len == 0)
                        {
                            dx += Math.random();
                            dy += Math.random();
                        }
                        else if (len < 100 * 100)
                        {
                            dx += vx / len;
                            dy += vy / len;
                        }
                    }
                }
                double d;
                double dlen = dx * dx + dy * dy;
                if (dlen > 0)
                {
                    dlen = Math.sqrt(dlen) / 2;
                    n1.dx += dx / dlen;
                    n1.dy += dy / dlen;
                }
             }
         }

         Dimension d = getSize();
         for (int i = 0 ; i < nodeCount ; i++)
         {

            if(nodes[i].visible == true)
            {
                Node n = nodes[i];
                if (!n.fixed)
                {
                    n.x += Math.max( -5, Math.min(5, n.dx));
                    n.y += Math.max( -5, Math.min(5, n.dy));
                }
                if (n.x < 0)
                {
                    n.x = 0;
                }
                else if (n.x > d.width)
                {
                    n.x = d.width;
                }
                if (n.y < 0)
                {
                    n.y = 0;
                }
                else if (n.y > d.height)
                {
                    n.y = d.height;
                }
                n.dx /= 2;
                n.dy /= 2;
            }
         }
         repaint();

    }//end relax

    Node pick;
    boolean pickfixed;
    Image offscreen;
    Dimension offscreensize;
    Graphics2D offgraphics;

    final Color fixedColor = new Color(150,51,0);
    final Color selectColor = Color.red;
    final Color edgeColor = new Color(80,0,0);
    Color fontColor = new Color(255,255,255);
    final Color nodeColor = new Color(55,57,145);
    final Color arcColor1 = new Color(80,0,0);
    final Color arcColor2 = new Color(80,0,0);
    final Color arcColor3 = new Color(140,0,0);
    final Color arcColor4 = new Color(140,0,0);
    final Color arcColor5 = new Color(255,20,20);
    final Color arcColor6 = new Color(255,60,60);
    final Color arcColor7 = new Color(255,120,120);
    final Color arcColor8 = new Color(255,180,180);

          Color foreGround = new Color(189,0,0);
          Color backGround = new Color(64,0,255);

          double numStep = 15;

          double delete1;
          double delete2;
          double delete3;
          double delete4;

    public void calculateDelete()
    {
           delete1 = (backGround.getRed()-foreGround.getRed())/numStep;
           delete2 = ((backGround.getGreen()-foreGround.getGreen())/numStep);
           delete3 = ((backGround.getBlue()-foreGround.getBlue())/numStep);
           delete4 = (15-0)/numStep;
    }

    public void paintNodeFadeIn(Graphics g, Node n)
    {
        int x = (int)n.x;
        int y = (int)n.y;

        g.setFont(new Font("Times New Roman",Font.BOLD,(int)(n.fontSize)));

        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(n.nodeName) + 10;
        int h = fm.getHeight() + 4;

        g.setColor(n.color.darker());
        g.drawRect(x - ((w/2)-3), y - ((h / 2)-3), w-3, h-3);
        g.drawRect(x - ((w/2)-2), y - ((h / 2)-2), w-3, h-3);

        g.setColor( (n == pick) ? selectColor : (n.fixed ? fixedColor : n.color));
        g.fillRect(x - w/2, y - h / 2, w, h);

        g.setColor(Color.black);
        g.drawRect(x - w/2, y - h / 2, w, h);

        g.setColor(fontColor);
        if(n.isParent == true){
        g.drawLine((x - w/2)+5, (y + h / 2)-4,( x + w/2)-5, (y + h / 2)-4);
        }
        g.drawString(n.nodeName, x - (w - 10) / 2, (y - (h - 4) / 2) + fm.getAscent());

        if(n.stepCount < numStep)
        {
            n.currentR = n.currentR - delete1;
            n.currentG = n.currentG - delete2;
            n.currentB = n.currentB - delete3;
            n.color = new Color((int)n.currentR,(int)n.currentG,(int)n.currentB);
            n.stepCount++;
            n.fontSize = n.fontSize + delete4;
        }

    }//end paintNodeFadeIn

    public void paintNodeFadeOut(Graphics g, Node n)
    {
        Dimension d = getSize();
        if(n.x < d.width)
        {
            if(n.x < centerNode.x)
            {
                n.x = n.x - 10;
            }
            else
            {
                n.x = n.x + 10;
            }
        }
        else
        {
            n.x = d.width;
        }
        if(n.y < d.height)
        {
            if(n.y < centerNode.y)
            {
                n.y = n.y - 10;
            }
            else
            {
                n.y = n.y + 10;
            }
        }
        else
        {
            n.y = d.height;
        }

        int x = (int)n.x;
        int y = (int)n.y;

        g.setFont(new Font("Arial",Font.BOLD,(int)(n.fontSize)));

        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(n.nodeName) + 10;
        int h = fm.getHeight() + 4;

        g.setColor(n.color.darker());
        g.drawRect(x - ((w/2)-3), y - ((h / 2)-3), w-3, h-3);
        g.drawRect(x - ((w/2)-2), y - ((h / 2)-2), w-1, h-1);

        g.setColor( (n == pick) ? selectColor : (n.fixed ? fixedColor : n.color));
        g.fillRect(x - w/2, y - h / 2, w, h);

        g.setColor(Color.black);
        g.drawRect(x - w/2, y - h / 2, w, h);

        g.setColor(fontColor);
        if(n.isParent == true)
        {
            g.drawLine((x - w/2)+5, (y + h / 2)-4,( x + w/2)-5, (y + h / 2)-4);
        }
        g.drawString(n.nodeName, x - (w - 10) / 2, (y - (h - 4) / 2) + fm.getAscent());

        if(n.stepCount < numStep)
        {
            n.currentR = n.currentR + delete1;
            n.currentG = n.currentG + delete2;
            n.currentB = n.currentB + delete3;
            n.color = new Color((int)n.currentR,(int)n.currentG,(int)n.currentB);
            n.stepCount++;
            n.fontSize = n.fontSize - delete4;
        }
        else
        {
            n.visible = false;
            n.fadeOut = false;
            n.fadeIn = false;
            n.isParent = false;
        }
    }//end paintNodeFadeOut

    public synchronized void update(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        Dimension d = getSize();
        if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height))
        {
            offscreen = createImage(d.width, d.height);
            offscreensize = d;
            if (offgraphics != null)
            {
                offgraphics.dispose();
            }
            offgraphics = (Graphics2D)offscreen.getGraphics();
            offgraphics.setFont(getFont());
        }

        offgraphics.setColor(getBackground());
        offgraphics.fillRect(0, 0, d.width, d.height);
        for (int i = 0 ; i < edgeCount ; i++) {
        Edge e = edges[i];

        if((nodes[e.from].visible == true) && (nodes[e.to].visible == true))
        {
            int x1 = (int) nodes[e.from].x;
            int y1 = (int) nodes[e.from].y;
            int x2 = (int) nodes[e.to].x;
            int y2 = (int) nodes[e.to].y;
            int len = (int) Math.abs(Math.sqrt( (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) - e.len);
            offgraphics.setColor( (len < 3) ? arcColor1 :
                     (len < 7 ? arcColor4 : (len < 12 ? arcColor5 : (len < 18 ? arcColor6 : (len < 25 ? arcColor7 : arcColor8)))));

            offgraphics.drawLine(x1, y1, x2, y2);
        }
    }

        for (int i = 0 ; i < nodeCount ; i++)
        {
            if(nodes[i].visible == true)
            {
                if(nodes[i].fadeOut == true)
                {
                    paintNodeFadeOut(offgraphics, nodes[i]);
                }
                if(nodes[i].fadeIn == true)
                {
                    paintNodeFadeIn(offgraphics, nodes[i]);
                }
            }
        }
        g.drawImage(offscreen, 0, 0, null);

    }//end update

    public void mouseClicked(MouseEvent e)
    {
        Node clickedNode = null;

        double bestdist = Double.MAX_VALUE;
        int x = e.getX();
        int y = e.getY();
        for (int i = 0 ; i < nodeCount ; i++)
        {
            if(nodes[i].visible == true)
            {
                Node n = nodes[i];
                double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
                if (dist < bestdist)
                {
                    clickedNode = n;
                    bestdist = dist;
                }
            }
        }
        manageVisible(clickedNode.nodeName);
      
        repaint();
        e.consume();

    }//end mouseClicked

    public void mousePressed(MouseEvent e)
    {
        double bestdist = Double.MAX_VALUE;
        int x = e.getX();
        int y = e.getY();
        for (int i = 0 ; i < nodeCount ; i++)
        {
            if(nodes[i].visible == true)
            {
                Node n = nodes[i];
                double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
                if (dist < bestdist)
                {
                    pick = n;
                    bestdist = dist;
                }
            }
        }
          pickfixed = pick.fixed;
          pick.fixed = true;
          pick.x = x;
          pick.y = y;
          repaint();
          e.consume();
    }//end mousePressed

    public void mouseReleased(MouseEvent e)
    {
        if (pick != null)
        {
            pick.x = e.getX();
            pick.y = e.getY();
            pick.fixed = pickfixed;
            pick = null;
        }
        repaint();
        e.consume();

    }//end mouseReleased

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mouseDragged(MouseEvent e)
    {
        pick.x = e.getX();
        pick.y = e.getY();
        repaint();
        e.consume();

    }//end mouseDragged

    public void mouseMoved(MouseEvent e){}

    public void start()
    {
        relaxer = new Thread(this);
        relaxer.start();
    }

    public void stop()
    {
        relaxer = null;
    }

    public void manageVisible(String ClickedButtonName)
    {

      if(ClickedButtonName.equalsIgnoreCase(""))
      {
        goCenter();

        if (!((OldCenterNode.nodeName).equalsIgnoreCase(NewCenterNode.nodeName)))
        {
          for (int i = 0; i < nodeCount; i++)
          {
            if ( (nodes[i].fixed == true) && (nodes[i] != pick))
            {
              centerNode = nodes[i];
              nodes[i].visible = true;
              nodes[i].fadeIn = true;
              nodes[i].fadeOut = false;
              nodes[i].color = backGround;
              nodes[i].stepCount = 0;
              nodes[i].fontSize = 0;
              nodes[i].currentR = backGround.getRed();
              nodes[i].currentG = backGround.getGreen();
              nodes[i].currentB = backGround.getBlue();
              calculateDelete();
              nodes[i].isParent = false;
            }
          }

          for (int i = 0; i < edgeCount; i++)
          {
            Edge e2 = edges[i];
            if ( (centerNode.nodeName).equalsIgnoreCase(nodes[e2.from].nodeName))
            {
               nodes[e2.to].visible = true;
               nodes[e2.to].fadeIn = true;
               nodes[e2.to].fadeOut = false;
               nodes[e2.to].color = backGround;
               nodes[e2.to].stepCount = 0;
               nodes[e2.to].fontSize = 0;
               nodes[e2.to].currentR = backGround.getRed();
               nodes[e2.to].currentG = backGround.getGreen();
               nodes[e2.to].currentB = backGround.getBlue();
               calculateDelete();
            }
            if ( (centerNode.nodeName).equalsIgnoreCase(nodes[e2.to].nodeName))
            {
               nodes[e2.from].visible = true;
               nodes[e2.from].fadeIn = true;
               nodes[e2.from].fadeOut = false;
               nodes[e2.from].color = backGround;
               nodes[e2.from].stepCount = 0;
               nodes[e2.from].fontSize = 0;
               nodes[e2.from].currentR = backGround.getRed();
               nodes[e2.from].currentG = backGround.getGreen();
               nodes[e2.from].currentB = backGround.getBlue();
               calculateDelete();
               nodes[e2.from].isParent = true;
            }
          }
          OldCenterNode.nodeName = NewCenterNode.nodeName;
        }
      }
        else
        { 
          boolean canChangeCenterNode = false;
          Node n = null;
          int node_no = 0;
          for (int i = 0; i < nodeCount; i++)
          {
            if (nodes[i].nodeName.equalsIgnoreCase(ClickedButtonName))
            {
              if (nodes[i].isLink == true)
              {
                canChangeCenterNode = true;
                NewCenterNode.nodeName = nodes[i].nodeName;
                n = nodes[i];
              }
              n = nodes[i];
            }
          }
          if (canChangeCenterNode == true)
          {
            if (!((OldCenterNode.nodeName).equalsIgnoreCase(NewCenterNode.nodeName)))
            {
              for (int i = 0; i < nodeCount; i++)
              {
                nodes[i].fixed = false;
                nodes[i].fadeIn = false;
                nodes[i].fadeOut = false;
                if (nodes[i].visible == true)
                {
                  nodes[i].fadeOut = true;
                  nodes[i].fadeIn = false;
                  nodes[i].color = foreGround;
                  nodes[i].stepCount = 0;
                  nodes[i].fontSize = 15;
                  nodes[i].currentR = foreGround.getRed();
                  nodes[i].currentG = foreGround.getGreen();
                  nodes[i].currentB = foreGround.getBlue();
                  calculateDelete();
                }
              }
              for (int i = 0; i < nodeCount; i++)
              {
                if (nodes[i].nodeName.equalsIgnoreCase(ClickedButtonName))
                {
                  nodes[i].fixed = true;
                  nodes[i].fadeIn = true;
                  nodes[i].fadeOut = false;
                  nodes[i].color = backGround;
                  nodes[i].stepCount = 0;
                  nodes[i].fontSize = 0;
                  nodes[i].currentR = backGround.getRed();
                  nodes[i].currentG = backGround.getGreen();
                  nodes[i].currentB = backGround.getBlue();
                  calculateDelete();

                  Dimension d = getSize();
                  manageVisible("");
                }
              }
            }
          }
          else
          {
            try
            {
              
              URL nodeUrl = new URL(graph.getCodeBase() + "frame1.html?param=" + n.nodeName);
              if ((n.nodeName).equalsIgnoreCase("Fish Fingers(Small)"))
                node_no=1000;
              else if ((n.nodeName).equalsIgnoreCase("Fish Fingers (Large)"))
                node_no=1001;
              else if ((n.nodeName).equalsIgnoreCase("Hamburger Patties"))
                node_no=1002;
              else if ((n.nodeName).equalsIgnoreCase("Shelled Prawns"))
                node_no=1003;
              else if ((n.nodeName).equalsIgnoreCase("Tub Ice Cream(1 Litre)"))
                node_no=1004;
              else if ((n.nodeName).equalsIgnoreCase("Tub Ice Cream(2 Litres)"))
                node_no=1005;
              else if ((n.nodeName).equalsIgnoreCase("Panadol Pack 24"))
                node_no=2000;
              else if ((n.nodeName).equalsIgnoreCase("Panadol Bottle 50"))
                node_no=2001;
              else if ((n.nodeName).equalsIgnoreCase("Bath Soap"))
                node_no=2002;
              else if ((n.nodeName).equalsIgnoreCase("Small (Pack 10)"))
                node_no=2003;
              else if ((n.nodeName).equalsIgnoreCase("Large (Pack 50)"))
                node_no=2004;
              else if ((n.nodeName).equalsIgnoreCase("Washing Powder"))
                node_no=2005;
              else if ((n.nodeName).equalsIgnoreCase("500 Grms"))
                node_no=3000;
              else if ((n.nodeName).equalsIgnoreCase("1000 Grms"))
                node_no=3001;
              else if ((n.nodeName).equalsIgnoreCase("TBone Steak"))
                node_no=3002;
              else if ((n.nodeName).equalsIgnoreCase("Navel Oranges"))
                node_no=3003;
              else if ((n.nodeName).equalsIgnoreCase("Bananas"))
                node_no=3004;
              else if ((n.nodeName).equalsIgnoreCase("Peaches"))
                node_no=3005;
              else if ((n.nodeName).equalsIgnoreCase("Grapes"))
                node_no=3006;
              else if ((n.nodeName).equalsIgnoreCase("Apples"))
                node_no=3007;
              else if ((n.nodeName).equalsIgnoreCase("Pack25"))
                node_no=4000;
              else if ((n.nodeName).equalsIgnoreCase("Pack 100"))
                node_no=4001;
              else if ((n.nodeName).equalsIgnoreCase("Pack 200"))
                node_no=4002;
              else if ((n.nodeName).equalsIgnoreCase("200 Grms"))
                node_no=4003;
              else if ((n.nodeName).equalsIgnoreCase("500 Grams"))
                node_no=4004;
              else if ((n.nodeName).equalsIgnoreCase("Chocolate Bar"))
                node_no=4005;
              else if ((n.nodeName).equalsIgnoreCase("5 Kgs"))
                node_no=5000;
              else if ((n.nodeName).equalsIgnoreCase("1 Kg"))
                node_no=5001;
              else if ((n.nodeName).equalsIgnoreCase("Bird Food"))
                node_no=5002;
              else if ((n.nodeName).equalsIgnoreCase("Cat Food"))
                node_no=5003;
              else if ((n.nodeName).equalsIgnoreCase("Fish Food"))
                node_no=5004;
              else if ((n.nodeName).equalsIgnoreCase("Laundry Bleach"))
                node_no=2006;
              

              URL nodeUrl1 = new URL("http://www-student.it.uts.edu.au/~kzhan/ipass2/item.php?RQ=" + node_no );
              graph.getAppletContext().showDocument(nodeUrl1, "rtop");
            }
            catch(MalformedURLException m)
            {
              System.out.println("error =" + m.toString());
            }
          }

        }

    }

    public void defineNode()
    {
        for(int x=0; x<nodeCount; x++)
        {
            nodes[x].isLink = false;
        }
        for (int i = 0; i < nodeCount; i++)
        {
            for (int j = 0; j < edgeCount; j++)
            {
                Edge ed = edges[j];
                if((nodes[i].nodeName).equals(nodes[ed.from].nodeName))
                {
                    nodes[i].isLink = true;
                }
            }
        }
        for (int i = 0; i < nodeCount; i++)
        {
            if(nodes[i].isLink == true)
            {
                nodes[i].nodeName += " >";
            }
        }

        for (int i = 0; i < nodeCount; i++)
        {
             for(int j=0 ; j<count ; j++)
             {
                 if(nodes[i].nodeName.equalsIgnoreCase(node_code[j][0]))
                 {
                     nodes[i].url = node_code[j][1];
                 }
             }
        }

        OldCenterNode.nodeName = "xxx";
        NewCenterNode.nodeName = "Grocery_Store";

    }//end defineNode



    public void printVisibleNode()
    {
        for (int i = 0; i < nodeCount; i++)
        {
             if(nodes[i].visible == true)
             {
                 System.out.println("visible node = " + nodes[i].nodeName);
             }
        }
    }//end printVisibleNode

    public void goCenter()
    {
        if((centerNode != null) && (centerNode != pick))
        {
             Dimension d = getSize();
             int x = d.width / 2;
             int y = d.height / 2;

             if((centerNode.x - 5) > x)
             {
                 centerNode.x = centerNode.x - 5;
             }
             else  if((centerNode.x > x))
             {
                 centerNode.x = x;
             }
             if((centerNode.x + 5) < x)
             {
                 centerNode.x = centerNode.x + 5;
             }
             else  if((centerNode.x < x))
             {
                 centerNode.x = x;
             }
             if((centerNode.y - 5) > y)
             {
                 centerNode.y = centerNode.y - 5;
             }else  if((centerNode.y > y))
             {
                 centerNode.y = y;
             }
             if((centerNode.y + 5) < y)
             {
                 centerNode.y = centerNode.y + 5;
             }else  if((centerNode.y < y))
             {
                 centerNode.y = y;
             }
        }
    }//end goCenter

}//end GraphPanel

public class Graph extends Applet implements ActionListener, ItemListener
{

    GraphPanel panel;
    Panel controlPanel;
    int count;
    String node_code[][] = new String[100][100];

    public void init()
    {
        setLayout(new BorderLayout());
       //setBackground(new Color(251,203,103)); 
        setBackground(new Color(255,255,255));

        panel = new GraphPanel(this);
        //panel.setBackground(new Color(251,203,103));
          panel.setBackground(new Color(255,255,255));
        add("Center", panel);
        controlPanel = new Panel();

        String edges = getParameter("edges");
        count = 0;
        for (StringTokenizer t = new StringTokenizer(edges, ",") ; t.hasMoreTokens() ; )
        {
            String str = t.nextToken();
            int i = str.indexOf('-');
            if (i > 0)
            {
                int len = 50;
                int k = str.indexOf("@");

                if(k > 0)
                {
                    node_code[count][1] = str.substring(k+1,k+5);
                    str = str.substring(0, k);
                }
                int j = str.indexOf('/');
                if (j > 0)
                {
                    len = Integer.valueOf(str.substring(j+1)).intValue();
                    str = str.substring(0, j);
                }
                panel.addEdge(str.substring(0,i), str.substring(i+1), len);
                if(k > 0)
                {
                    node_code[count++][0] = str.substring(i+1);
                }

            }
        }

        for(int x=0 ; x<count ;x++)
        {
          GraphPanel.node_code[x][0] =  node_code[x][0];
          GraphPanel.node_code[x][1] =  node_code[x][1];
        } GraphPanel.count = count;


        Dimension d = getSize();
        String center = getParameter("center");

        if (center != null)
        {
            Node n = panel.nodes[panel.findNode(center)];
            n.x = d.width / 2;
            n.y = d.height / 2;
            n.fixed = true;
        }
    }//end init

    public void destroy()
    {
        remove(panel);
        remove(controlPanel);
    }

    public void start()
    {
        panel.start();
    }

    public void stop()
    {
        panel.stop();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
    }

    public void itemStateChanged(ItemEvent e)
    {
        Object src = e.getSource();
        boolean on = e.getStateChange() == ItemEvent.SELECTED;
    }

    public String[][] getParameterInfo()
    {
        String[][] info =
        {
        {"edges", "delimited string", "A comma-delimited list of all the edges.  It takes the form of 'C-N1,C-N2,C-N3,C-NX,N1-N2/M12,N2-N3/M23,N3-NX/M3X,...' where C is the name of center node (see 'center' parameter) and NX is a node attached to the center node.  For the edges connecting nodes to eachother (and not to the center node) you may (optionally) specify a length MXY separated from the edge name by a forward slash."},
        {"center", "string", "The name of the center node."}
        };
    return info;
    }

}//end Graph

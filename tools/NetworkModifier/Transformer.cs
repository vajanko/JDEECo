using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Matsim;
using System.Xml.Linq;
using System.Globalization;
using Matsim;
using Matsim.Generator;

namespace Matsim.Transformer
{
    class Transformer
    {
        static void Main(string[] args)
        {
            string inputFile = "c:\\Users\\Ondrej\\Downloads\\maps\\berlin-extracted.xml";
            string outputFile = "c:\\Users\\Ondrej\\Downloads\\maps\\network.xml";

            double coef = 100000;

            XDocument doc = XDocument.Load(inputFile);
            XElement net = doc.Root;

            XElement nodesElem = net.Element("nodes");
            foreach (XElement node in nodesElem.Elements("node"))
            {
                double x = node.GetX() * coef - 1300000;
                double y = node.GetY() * coef - 5200000;
                node.SetAttributeValue("x", x);
                node.SetAttributeValue("y", y);
            }

            Graph graph = Graph.LoadNetwork(net);

            int linksTransformed = 0;
            XElement linksElem = net.Element("links");
            int maxLinkId = 0;
            foreach (XElement link in linksElem.Elements("link"))
            {
                //double length = double.Parse(link.GetAttr("length"), CultureInfo.InvariantCulture);
                //if (length == 0)
                //{
                //    throw new NotSupportedException();
                //}
                //link.SetAttributeValue("length", length * coef);

                Node f = graph.GetNode(link.GetFrom());
                Node t = graph.GetNode(link.GetTo());
                double dist = PositionHelper.GetDistance(f.Position, t.Position);
                link.SetAttributeValue("length", dist);
                
                linksTransformed++;

                int linkId;
                if (int.TryParse(link.GetId(), out linkId) && linkId > maxLinkId)
                    maxLinkId = linkId;
            }

            
            int linksAdded = 0;
            foreach (XElement link in linksElem.Elements("link").ToArray())
            {
                string f = link.GetFrom();
                string t = link.GetTo();

                // check whether there is the reverse edge
                if (!graph.HasEdge(t, f))
                {
                    XElement revLink = new XElement("link",
                        new XAttribute("id", ++maxLinkId),
                        new XAttribute("from", t),
                        new XAttribute("to", f),
                        new XAttribute("length", link.GetAttr("length")),
                        new XAttribute("freespeed", link.GetAttr("freespeed")),
                        new XAttribute("capacity", link.GetAttr("capacity")),
                        new XAttribute("oneway", 1),
                        new XAttribute("permlanes", 1),
                        new XAttribute("modes", link.GetAttr("modes")));

                    linksElem.Add(revLink);
                    linksAdded++;
                }
            }

            // TODO: divide links
            

            doc.Save(outputFile);

            Console.WriteLine("{0} links transformed.", linksTransformed);
            Console.WriteLine("{0} links added.", linksAdded);
            Console.WriteLine("Press any key to continue ...");
            Console.ReadKey();
        }
    }
}

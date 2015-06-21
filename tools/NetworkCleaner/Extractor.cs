using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using Matsim;
using Matsim.Generator;

namespace Matsim.Extractor
{
    class Extractor
    {
        private static double distance(Position a, Position b)
        {
            double x = a.X - b.X;
            double y = a.Y - b.Y;
            return Math.Sqrt(x * x + y * y);
        }

        static void Main(string[] args)
        {
            string inputFile = "c:\\Users\\Ondrej\\Downloads\\maps\\berlin-original.xml";
            string outputFile = "c:\\Users\\Ondrej\\Downloads\\maps\\berlin-extracted.xml";
            Position center = new Position(13.4130078, 52.491415);
            double maxDistance = 0.04;

            XDocument doc = XDocument.Load(inputFile);
            XElement net = doc.Root;

            HashSet<string> nodes = new HashSet<string>();
            int nodesRemoved = 0;
            XElement nodesElem = net.Element("nodes");
            foreach (XElement node in nodesElem.Elements("node").ToArray())
            {
                Position pos = node.GetPosition();
                double dist = distance(center, pos);
                if (dist > maxDistance)
                {
                    node.Remove();
                    nodesRemoved++;
                }
                else
                {
                    nodes.Add(node.GetId());
                }
            }

            HashSet<string> linkNodes = new HashSet<string>();
            int linksRemoved = 0;
            XElement linksElem = net.Element("links");
            foreach (XElement link in linksElem.Elements("link").ToArray())
            {
                string from = link.GetFrom();
                string to = link.GetTo();
                if (!nodes.Contains(from) || !nodes.Contains(to))
                {
                    link.Remove();
                    linksRemoved++;
                }
                else
                {
                    linkNodes.Add(from);
                    linkNodes.Add(to);
                }
            }

            // remove lonely nodes
            foreach (XElement node in nodesElem.Elements("node").ToArray())
            {
                string nodeId = node.GetId();
                if (!linkNodes.Contains(nodeId))
                {
                    node.Remove();
                    nodesRemoved++;
                }
            }


            doc.Save(outputFile);

            Console.WriteLine("Removed {0} nodes", nodesRemoved);
            Console.WriteLine("Removed {0} links", linksRemoved);

            //Console.WriteLine("Checking reachability ... ");
            //Graph graph = Graph.LoadNetwork(net);

            Console.WriteLine("Press any key to continue ...");
            Console.ReadKey();
        }
    }
}

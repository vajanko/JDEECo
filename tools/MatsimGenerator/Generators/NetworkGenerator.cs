using Matsim.Generator;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using Matsim;

namespace Matsim.Generator
{
    class NetworkGenerator : GeneratorBase
    {
        protected IdProvider nodeIds = new IdProvider();
        protected IdProvider linkIds = new IdProvider();

        protected double length = 1000;
        protected double freespeed = 8;
        protected double capacity = 2000;
        protected string[] modes = { "car", "pt" };

        public int Width { get; set; }
        public int Height { get; set; }
        public double Step { get; set; }
        public double Left { get; set; }
        public double Bottom { get; set; }

        //public int NodeCount { get { return Width * Height; } }
        //public int LinkCount { get { return ((Width - 1) * Height + Width * (Height - 1)) * 2; } }

        //private int getCoordX(int nodeId)
        //{
        //    return (nodeId - 1) % Width;
        //}
        //private int getCoordY(int nodeId)
        //{
        //    return (nodeId - 1) / Width;
        //}

        //public IEnumerable<XElement> GetDirectoRoute(string fromLinkId, string toLinkId)
        //{
        //    XElement fromLink = GetLink(fromLinkId);
        //    string nodeA = fromLink.GetFrom();
        //    int ax = getCoordX(nodeA);
        //    int ay = getCoordY(nodeA);

        //    XElement toLink = GetLink(toLinkId);
        //    string nodeB = toLink.GetTo();
        //    int bx = getCoordX(nodeB);
        //    int by = getCoordY(nodeB);

            
        //    List<XElement> route = new List<XElement>();

        //    while (ax != bx)
        //    {
        //        if (ax < bx)
        //            ax++;
        //        else
        //            ax--;

        //        int nextNode = ay * Width + ax + 1;
        //        XElement link = GetLink(nodeA, nextNode);
        //        route.Add(link);
        //        nodeA = nextNode;
        //    }
        //    while (ay != by)
        //    {
        //        if (ay < by)
        //            ay++;
        //        else
        //            ay--;

        //        int nextNode = ay * Width + ax + 1;
        //        XElement link = GetLink(nodeA, nextNode);
        //        route.Add(link);
        //        nodeA = nextNode;
        //    }

        //    return route;
        //}
        //public IEnumerable<XElement> GetLinks()
        //{
        //    return Root.Element("links").Elements();
        //}
        //public XElement GetLink(string id)
        //{
        //    return GetLinks().Where(el => el.GetId() == id).SingleOrDefault();
        //}
        //public XElement GetLink(string from, string to)
        //{
        //    return GetLinks().Where(el => el.GetFrom() == from && el.GetTo() == to).SingleOrDefault();
        //}
        //public Position GetLinkPosition(XElement link)
        //{
        //    XElement from = GetNode(link.GetFrom());
        //    XElement to = GetNode(link.GetTo());

        //    return new Position((from.GetX() + to.GetX()) / 2, (from.GetY() + to.GetY()) / 2);
        //}
        //public Position GetLinkPosition(string id)
        //{
        //    return GetLinkPosition(GetLink(id));
        //}
        //public IEnumerable<XElement> GetNodes()
        //{
        //    return Root.Element("nodes").Elements();
        //}
        //public XElement GetNode(string id)
        //{
        //    return GetNodes().Where(el => el.GetId() == id).SingleOrDefault();
        //}

        public NetworkGenerator(ConfigGenerator conf)
            : base(conf, "Templates/network.xml", "network.xml", "Templates/network_v1.dtd")
        {
            this.Width = 10;
            this.Height = 10;
            this.Step = 10;
            this.Left = 0;
            this.Bottom = 0;
            
        }

        protected override void generateInternal(XDocument doc)
        {
            XElement net = doc.Root;
            net.SetAttributeValue("name", this.GetType().Name);

            // generate nodes
            XElement nodes = net.Element("nodes");
            for (int j = 0; j < Height; ++j)
            {
                for (int i = 0; i < Width; ++i)
                {
                    double x = Left + i * Step;
                    double y = Bottom + j * Step;
                    nodes.Add(createNode(x, y));
                }
            }

            // connect with links
            XElement links = net.Element("links");
            for (int j = 0; j < Height; ++j)
            {
                for (int i = 0; i < Width; ++i)
                {
                    int current = j * Width + i + 1;
                    int upper = (j + 1) * Width + i + 1;
                    int right = current + 1;

                    if (j < Height - 1)
                    {
                        links.Add(createLink(current, upper));
                        links.Add(createLink(upper, current));
                    }

                    if (i < Width - 1)
                    {
                        links.Add(createLink(current, right));
                        links.Add(createLink(right, current));
                    }
                }
            }
        }

        protected XElement createNode(double x, double y)
        {
            string id = nodeIds.CreateId();

            XElement node = new XElement("node",
                new XAttribute("id", id),
                new XAttribute("x", x),
                new XAttribute("y", y));

            return node;
        }
        protected XElement createLink(int from, int to)
        {
            string id = linkIds.CreateId();

            XElement link = new XElement("link",
                new XAttribute("id", id),
                new XAttribute("from", from),
                new XAttribute("to", to),
                new XAttribute("length", Step),
                new XAttribute("freespeed", freespeed),
                new XAttribute("capacity", capacity),
                new XAttribute("oneway", 1),
                new XAttribute("permlanes", 1),
                new XAttribute("modes", string.Join(",", modes)));

            return link;
        }
    }
}

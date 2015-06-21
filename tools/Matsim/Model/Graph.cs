using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using System.Globalization;
using Matsim;


namespace Matsim.Generator
{
    public class Graph
    {
        private class TraverseInfo : IComparable<TraverseInfo>
        {
            public double Distance;
            public Node Node;
            public Edge Edge;

            public TraverseInfo(double distance, Node node, Edge edge)
            {
                this.Distance = distance;
                this.Node = node;
                this.Edge = edge;
            }

            public int CompareTo(TraverseInfo other)
            {
                return Distance.CompareTo(other.Distance);
            }
        }

        private Dictionary<string, Node> nodes = new Dictionary<string, Node>();
        private Dictionary<string, Edge> edges = new Dictionary<string, Edge>();
        private Dictionary<Node, IEnumerable<Edge>> graph = new Dictionary<Node, IEnumerable<Edge>>();

        public int EdgeCount { get { return edges.Count; } }
        public int NodeCount { get { return nodes.Count; } }

        public Node GetNode(string id)
        {
            return nodes[id];
        }
        public Edge GetEdge(string id)
        {
            return edges[id];
        }
        public bool HasEdge(string from, string to)
        {
            Node f = GetNode(from);
            Node t = GetNode(to);

            return graph[f].Any(e => e.To == t);
        }

        public IEnumerable<Edge> GetEdges()
        {
            return edges.Values;
        }
        public IEnumerable<Edge> FindShortestPath(Node from, Node to)
        {
            Dictionary<Node, TraverseInfo> trav = new Dictionary<Node, TraverseInfo>();
            foreach (Node n in nodes.Values)
                trav.Add(n, new TraverseInfo(double.MaxValue, n, null));
            trav[from].Distance = 0;

            SortedSet<TraverseInfo> unprocessed = new SortedSet<TraverseInfo>(trav.Values);

            while (unprocessed.Count > 0)
            {
                var info = unprocessed.Min;
                if (info.Distance == double.MaxValue)
                    throw new NotSupportedException("Graph is not stongly connected.");

                unprocessed.Remove(info);
                Node u = info.Node;

                if (u == to)
                    break;

                // process neighbors
                foreach (Edge e in graph[u])
                {
                    TraverseInfo ti = trav[e.To];
                    double len = trav[u].Distance + e.Length;
                    if (len < ti.Distance)
                    {
                        unprocessed.Remove(ti);
                        ti.Distance = len;
                        ti.Edge = e;
                        unprocessed.Add(ti);
                    }
                }
            }

            

            //List<Node> unprocessed = new List<Node>(nodes.Values);

            //while (unprocessed.Count > 0)
            //{
            //    Node u = unprocessed.OrderBy(n => trav[n].Distance).First();

            //    var info = trav[u];
            //    if (info.Distance == double.MaxValue)
            //        throw new NotSupportedException("Graph is not stongly connected.");
            //    unprocessed.Remove(u);

            //    if (u == to)
            //        break;

            //    // process neighbors
            //    foreach (Edge e in graph[u])
            //    {
            //        Node n = e.To;
            //        double len = trav[u].Distance + e.Length;
            //        if (len < trav[n].Distance)
            //        {
            //            trav[n].Distance = len;
            //            trav[n].Edge = e;
            //        }
            //    }
            //}

            List<Edge> path = new List<Edge>();
            Node node = to;
            while (node != from)
            {
                Edge edge = trav[node].Edge;
                path.Add(edge);
                node = edge.From;
            }
            path.Reverse();

            return path;
        }

        private Graph(IEnumerable<Node> nodes, IEnumerable<Edge> edges)
        {
            foreach (Node n in nodes)
            {
                List<Edge> outEdges = new List<Edge>();
                foreach (Edge e in edges)
                {
                    if (n == e.From)
                        outEdges.Add(e);
                }

                this.graph.Add(n, outEdges);
                this.nodes.Add(n.Id, n);
            }

            foreach (Edge e in edges)
            {
                this.edges.Add(e.Id, e);
            }
        }

        public static Graph LoadNetwork(XElement net)
        {
            Dictionary<string, Node> nodes = new Dictionary<string, Node>();
            List<Edge> edges = new List<Edge>();

            IEnumerable<XElement> xnodes = net.Element("nodes").Elements();
            foreach (XElement xnode in xnodes)
            {
                string id = xnode.GetId();
                Node node = new Node(id, xnode.GetPosition());
                nodes.Add(id, node);
            }

            IEnumerable<XElement> xedges = net.Element("links").Elements();
            foreach (XElement xedge in xedges)
            {
                Node from = nodes[xedge.GetFrom()];
                Node to =  nodes[xedge.GetTo()];

                double freespeed = 14;  // 50 km/h
                string speed = xedge.GetAttr("freespeed");
                if (!string.IsNullOrEmpty(speed))
                    freespeed = double.Parse(speed, CultureInfo.InvariantCulture);

                Edge edge = new Edge(xedge.GetId(), from, to, xedge.GetDoubleAttr("length"), freespeed);
                edges.Add(edge);
            }

            Graph graph = new Graph(nodes.Values, edges);
            return graph;
        }
    }
}

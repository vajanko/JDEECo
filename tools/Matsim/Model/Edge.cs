using Matsim;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Matsim.Generator
{
    public class Edge
    {
        public string Id { get; private set; }
        public Node From { get; private set; }
        public Node To { get; private set; }
        public double Length { get; private set; }
        public int Capacity { get; set; }
        public double Freespeed { get; private set; }
        public int Permlanes { get; set; }

        /// <summary>
        /// Calcualated position of an edge
        /// </summary>
        public Position Position { get; private set; }

        public Edge(string id, Node from, Node to, double length, double freespeed)
        {
            if (id == null)
                throw new ArgumentNullException("id");

            this.Id = id;
            this.From = from;
            this.To = to;
            this.Length = length;
            this.Freespeed = freespeed;

            this.Position = new Position((from.Position.X + to.Position.X) / 2, (from.Position.Y + to.Position.Y) / 2);
        }

        public override string ToString()
        {
            return string.Format("{0}-{1}", From, To);
        }
    }
}

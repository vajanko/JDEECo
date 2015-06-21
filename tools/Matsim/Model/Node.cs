using Matsim;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Matsim.Generator
{
    public class Node : IComparable<Node>
    {
        public string Id { get; private set; }
        public Position Position { get; private set; }

        public Node(string id, Position position)
        {
            if (id == null)
                throw new ArgumentNullException("id");

            this.Id = id;
            this.Position = position;
        }

        public int CompareTo(Node other)
        {
            return Id.CompareTo(other.Id);
        }

        public override string ToString()
        {
            return string.Format("[{0}]", Id);
        }
    }
}

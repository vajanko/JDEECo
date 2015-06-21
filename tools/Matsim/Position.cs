using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Matsim
{
    public class Position
    {
        public double X;
        public double Y;

        public Position()
            : this(0, 0)
        {

        }
        public Position(double x, double y)
        {
            this.X = x;
            this.Y = y;
        }
    }
}

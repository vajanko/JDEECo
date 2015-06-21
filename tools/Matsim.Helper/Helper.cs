using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;
using Matsim;

namespace Matsim.Helper
{
    class Helper
    {
        static void Main(string[] args)
        {
            double minX = double.MaxValue;
            double minY = double.MaxValue;
            double maxX = double.MinValue;
            double maxY = double.MinValue;

            XElement net = XElement.Load(@"c:\Projects\JDEECo\smart-taxi-demo\config\matsim\berlin\network.xml");
            foreach (XElement node in net.Element("nodes").Elements())
            {
                double x = node.GetX();
                double y = node.GetY();

                if (x < minX) minX = x;
                else if (x > maxX) maxX = x;

                if (y < minY) minY = y;
                else if (y > maxY) maxY = y;
            }

            Console.WriteLine("minX = {0}", minX);
            Console.WriteLine("maxX = {0}", maxX);
            Console.WriteLine("minY = {0}", minY);
            Console.WriteLine("maxY = {0}", maxY);
            Console.ReadKey();
        }
    }
}

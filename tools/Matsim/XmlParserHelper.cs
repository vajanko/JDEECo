using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Matsim
{
    public static class XmlParserHelper
    {
        public static string GetAttr(this XElement elem, string name)
        {
            return elem.Attribute(name).Value;
        }
        public static string GetId(this XElement elem)
        {
            return elem.GetAttr("id");
        }
        public static string GetFrom(this XElement elem)
        {
            return elem.GetAttr("from");
        }
        public static string GetTo(this XElement elem)
        {
            return elem.GetAttr("to");
        }
        public static double GetX(this XElement elem)
        {
            return elem.GetDoubleAttr("x");
        }
        public static double GetY(this XElement elem)
        {
            return elem.GetDoubleAttr("y");
        }
        public static int GetIntAttr(this XElement elem, string name)
        {
            return int.Parse(elem.GetAttr(name));
        }
        public static double GetDoubleAttr(this XElement elem, string name)
        {
            return double.Parse(elem.GetAttr(name), CultureInfo.InvariantCulture);
        }
        public static Position GetPosition(this XElement elem)
        {
            return new Position(elem.GetX(), elem.GetY());
        }
    }
}

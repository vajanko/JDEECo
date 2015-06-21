using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Matsim.Generator
{
    class IdProvider
    {
        private int lastId = 1;

        public string CreateId()
        {
            return (lastId++).ToString();
        }

        public XAttribute CreateIdAttr()
        {
            return new XAttribute("id", CreateId());
        }
    }
}

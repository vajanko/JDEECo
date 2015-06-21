using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Matsim.Generator
{
    class CloneNetworkGenerator : GeneratorBase
    {
        public CloneNetworkGenerator(ConfigGenerator conf, string inputFile)
            : base(conf, inputFile, Path.GetFileName(inputFile), "Templates/network_v1.dtd")
        {

        }

        protected override void generateInternal(XDocument doc)
        {
            // TODO: replace DTD file
        }
    }
}

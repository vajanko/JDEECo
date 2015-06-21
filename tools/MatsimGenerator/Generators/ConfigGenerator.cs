using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Matsim.Generator
{
    class ConfigGenerator : IMatsimGenerator
    {
        public Random Generator { get; private set; }

        public string RootDir { get; private set; }
        public string ConfigDir { get; private set; }
        public string OutputDir { get; private set; }
        public TimeSpan Start { get; private set; }
        public TimeSpan End { get; private set; }
        public string NetworkFilename { get; private set; }
        

        public ConfigGenerator(string rootDir, string configDir, string outputDir, TimeSpan start, TimeSpan end, string networkFilename)
        {
            this.Generator = new Random(123);

            this.RootDir = rootDir;
            this.ConfigDir = configDir;
            this.OutputDir = outputDir;
            this.Start = start;
            this.End = end;
            this.NetworkFilename = networkFilename;
        }
        public void Generate()
        {
            string template = File.ReadAllText("Templates/config.xml");
            StringBuilder builder = new StringBuilder(template);

            builder.Replace("${config}", ConfigDir);
            builder.Replace("${output}", OutputDir);
            builder.Replace("${start}", Start.ToString());
            builder.Replace("${end}", End.ToString());
            builder.Replace("${network}", NetworkFilename);

            string targetDir = Path.Combine(RootDir, ConfigDir);
            Directory.CreateDirectory(targetDir);
            string targetFile = Path.Combine(targetDir, "config.xml");
            File.WriteAllText(targetFile, builder.ToString());

            File.Copy("Templates/config_v1.dtd", Path.Combine(targetDir, "config_v1.dtd"), true);
        }
    }
}

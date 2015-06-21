using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Matsim.Generator
{
    abstract class GeneratorBase : IMatsimGenerator
    {
        protected ConfigGenerator config;

        public XElement Root { get; private set; }
        public string TemplateFile { get; private set; }
        public string OutputFile { get; private set; }
        public string DtdFile { get; private set; }

        public GeneratorBase(ConfigGenerator conf, string templateFile, string outputFile, string dtdFile)
        {
            this.config = conf;
            this.TemplateFile = templateFile;
            this.OutputFile = Path.Combine(conf.RootDir, conf.ConfigDir, outputFile);
            this.DtdFile = dtdFile;
        }

        private XDocument loadTemplate()
        {
            XDocument doc = XDocument.Load(TemplateFile);
            Root = doc.Root;

            return doc;
        }
        private void saveOutput(XDocument doc)
        {
            doc.Save(OutputFile);
        }

        /// <summary>
        /// Random number in interval [0,max)
        /// </summary>
        /// <param name="max"></param>
        protected int random(int max)
        {
            return config.Generator.Next(max);
        }

        public void Generate()
        {
            // copy DTD file to the output
            string dtdOutput = Path.Combine(Path.GetDirectoryName(OutputFile), Path.GetFileName(DtdFile));
            File.Copy(DtdFile, dtdOutput, true);

            // load XML file from template ...
            XDocument doc = loadTemplate();

            // change DTD file locaiton to the local one to prevent downloading it from the internet
            // relative path to the output directory where dtd files are places
            if (doc.DocumentType != null)
                doc.DocumentType.SystemId = Path.Combine(config.ConfigDir, Path.GetFileName(DtdFile));

            // ... and modify it
            generateInternal(doc);

            // save to output directory
            saveOutput(doc);
        }

        protected abstract void generateInternal(XDocument doc);
    }
}

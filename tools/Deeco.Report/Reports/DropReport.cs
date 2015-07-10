using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class DropReport : IReport
    {
        private string inputPath;
        public DropReport(string inputPath)
        {
            this.inputPath = inputPath;
        }
        public void GenerateReport(string outputFile)
        {
            List<string> files = new List<string>();
            foreach (string part in inputPath.Split(';'))
            {
                if (File.Exists(part))
                    files.Add(part);
                else
                    files.AddRange(Directory.EnumerateFiles(part, "*.out"));
            }

            int count = files.Sum(f => File.ReadAllLines(f).Where(l => l == "Dropped message").Count());
            File.WriteAllText(outputFile, string.Format("Count\n{0}", count));
        }
    }
}

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class CombineReport : IReport
    {
        private Dictionary<string, IReport> reports = new Dictionary<string, IReport>();

        public void AddReport(string name, IReport report)
        {
            this.reports.Add(name, report);
        }

        public void GenerateReport(string outputFile)
        {
            File.Delete(outputFile);
            using (StreamWriter writer = new StreamWriter(outputFile))
            {
                bool firstLine = true;

                foreach (var pair in this.reports)
                {
                    string tmpFile = Path.GetTempFileName();
                    pair.Value.GenerateReport(tmpFile);

                    var lines = File.ReadAllLines(tmpFile);
                    if (firstLine)
                    {
                        writer.WriteLine("Type;{0}", lines.First());
                        firstLine = false;
                    }

                    foreach (string line in lines.Skip(1))
                        writer.WriteLine("{0};{1}", pair.Key, line);

                    File.Delete(tmpFile);
                }
            }
        }
    }
}

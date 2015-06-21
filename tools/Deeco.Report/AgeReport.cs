using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class AgeReport
    {
        private RequestSource source;
        private string outputFile;

        public AgeReport(string inputPath, string outputFile)
        {
            this.source = new RequestSource(inputPath);
            this.outputFile = outputFile;
        }

        public void GenerateReport()
        {
            using (var writer = new StreamWriter(File.OpenWrite(outputFile)))
            {
                writer.WriteLine("Version;WBottom;Box1;Box2;Box3;WTop");
                foreach (var item in this.source.GetRequests()
                    // select retransmited messages
                    .Where(r => r.GetString("Action") == "SEND" && r.GetInt("IsSource") == 0)
                    .Select(r => new
                    {
                        Version = string.Format("P:{0:F1} C:{1}", r.GetDouble("Probability"), r.GetString("PublishCount")),
                        Age = r.GetInt("Age"),
                    })
                    .GroupBy(i => i.Version)
                    .Select(g => new
                    {
                        Version = g.Key,
                        Min = g.Min(i => i.Age),
                        Max = g.Max(i => i.Age),
                        Median = g.Median(i => i.Age),
                        Q1 = 1,
                        Q3 = 3,
                    })
                    .Select(i => new
                    {
                        Version = i.Version,
                        WBottom = i.Q1 - i.Median,
                        Box1 = i.Q1,
                        Box2 = i.Median - i.Q1,
                        Box3 = i.Q3 - i.Median,
                        WTop = i.Max - i.Q3
                    })
                    )
                {
                    writer.WriteLine("{0};{1};{2};{3};{4};{5}");
                }
            }
        }
    }
}

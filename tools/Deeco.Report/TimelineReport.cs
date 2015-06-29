using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    /// <summary>
    /// Calculates the number of SENT messages per time
    /// </summary>
    class TimelineReport : IReport
    {
        private RequestSource source;
        private Func<Request, string> versionSelector;

        public int Timestep { get; set; }

        public TimelineReport(string inputPath, Func<Request, string> versionSelector)
        {
            this.source = new RequestSource(inputPath);
            this.versionSelector = versionSelector;
            this.Timestep = 1000;
        }

        public void GenerateReport(string outputFile)
        {
            File.Delete(outputFile);
            using (var writer = new StreamWriter(File.OpenWrite(outputFile)))
            {
                writer.WriteLine("Version;Time;Count");
                foreach (var item in this.source.GetRequests()
                    .Where(r => r.GetString("Action") == "SEND")
                    .Select(r => new
                    {
                        Version = versionSelector(r),
                        Time = r.GetInt("Time"),
                    })
                    .GroupBy(i => i.Version)
                    .SelectMany(g1 => 
                        g1.GroupBy(i => i.Time / Timestep)
                        .Select(g2 => new 
                        { 
                            Version = g1.Key, 
                            Time = g2.Key, 
                            Count = g2.Count() 
                        })
                    )
                    .OrderBy(i => i.Version).ThenBy(i => i.Time)
                    )
                {
                    writer.WriteLine("{0};{1};{2}", item.Version, item.Time, item.Count);
                }
            }
        }
    }
}

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class RebroadcastReport : ReportBase
    {
        public int Timestep { get; set; }

        public RebroadcastReport(string inputPath, Func<Request, string> versionSelector)
            : base(inputPath, versionSelector)
        {
            Timestep = 1000;
        }

        protected override void writeReport(StreamWriter writer)
        {
            writer.WriteLine("Version;Time;Count");
            foreach (var req in this.GetRequests()
                // select retransmited messages
                .Where(r => r.GetString("Action") == "SEND" && r.GetInt("IsSource") == 0)
                .Select(r => new 
                {
                    Version = GetVersion(r),
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
                ))
            {
                writer.WriteLine("{0};{1};{2}", req.Version, req.Time, req.Count);
            }
        }
    }
}

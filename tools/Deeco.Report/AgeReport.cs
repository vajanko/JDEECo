using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class AgeReport : ReportBase
    {
        public int Timestep { get; set; }

        public AgeReport(string inputPath, Func<Request, string> versionSelector)
            : base(inputPath, versionSelector)
        {
            this.Timestep = 1000;
        }

        protected override void writeReport(StreamWriter writer)
        {
            writer.WriteLine("Version;WBottom;Box1;Box2;Box3;WTop");

            foreach (var item in this.GetRequests()
                // select retransmited messages
                .Where(r => r.GetString("Action") == "SEND")// && r.GetInt("IsSource") == 0)
                .Select(r => new
                {
                    Version = GetVersion(r),
                    Age = r.GetInt("Age"),
                })
                .GroupBy(i => i.Version)
                .Select(g => new
                {
                    Version = g.Key,
                    Min = g.Min(i => i.Age),
                    Max = g.Max(i => i.Age),
                    Median = g.Median(i => i.Age),
                    Q1 = g.Quartil1(i => i.Age),
                    Q3 = g.Quartil3(i => i.Age),
                })
                .Select(i => new
                {
                    Version = i.Version,
                    WBottom = i.Q1 - i.Min,
                    Box1 = i.Q1,
                    Box2 = i.Median - i.Q1,
                    Box3 = i.Q3 - i.Median,
                    WTop = i.Max - i.Q3
                }))
            {
                writer.WriteLine("{0};{1};{2};{3};{4};{5}", item.Version, item.WBottom, item.Box1, item.Box2, item.Box3, item.WBottom);
            }
        }

        
    }
}

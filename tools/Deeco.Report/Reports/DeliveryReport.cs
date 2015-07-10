using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class DeliveryReport : ReportBase
    {
        public DeliveryReport(string inputPath, Func<Request, string> versionSelector)
            : base(inputPath, versionSelector)
        {

        }
        protected override void writeReport(StreamWriter writer)
        {
            writer.WriteLine("Version;Component;Count");

            foreach (var item in this.GetRequests()
                .Where(r => r.GetString("Action") == "RECV")
                .Select(r => new
                {
                    Version = GetVersion(r),
                    Component = int.Parse(r.GetString("ComponentId").Substring(2)),
                    Node = r.GetInt("Node")
                })
                .GroupBy(i => i.Version)
                .SelectMany(g1 =>
                    g1.GroupBy(i => i.Component)
                    .Select(g2 => new
                    {
                        Version = g1.Key,
                        Component = g2.Key,
                        Count = g2.Select(i => i.Node).Distinct().Count()
                    })
                    
                ))
            {
                writer.WriteLine("{0};{1};{2}", item.Version, item.Component, item.Count);
            }
        }

        //protected override void writeReport(StreamWriter writer)
        //{
        //    writer.WriteLine("Version;Source;Target;Count");

        //    foreach (var item in this.GetRequests()
        //        .Where(r => r.GetString("Action") == "RECV")
        //        .Select(r => new
        //        {
        //            Version = GetVersion(r),
        //            Source = int.Parse(r.GetString("ComponentId").Substring(2)),
        //            Target = r.GetInt("Node"),
        //        })
        //        .GroupBy(i => i.Version)
        //        .SelectMany(g1 =>
        //            g1.GroupBy(r => r.Target)
        //            .SelectMany(g2 => g2.GroupBy(r => r.Source).Select(g3 => new 
        //                {
        //                    Version = g1.Key,
        //                    Source = g3.Key, 
        //                    Target = g2.Key, 
        //                    Count = g3.Count() 
        //                }))
        //        ))
        //    {
        //        writer.WriteLine("{0};{1};{2};{3}", item.Version, item.Source, item.Target, item.Count);
        //    }
        //}
    }
}

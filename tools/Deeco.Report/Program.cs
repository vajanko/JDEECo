using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class Program
    {
        const string root = "C:\\tmp\\jdeeco-reports";

        static string GetPath(string file)
        {
            return Path.Combine(root, file);
        }
        static void manet()
        {
            Func<Request, string> version = new Func<Request, string>(r => string.Format("P:{0:F1}", r.GetDouble("Probability")));
            string simpleInput = GetPath("results\\manet-simple.csv");
            string boundaryInput = GetPath("results\\manet-boundary.csv");

            TimelineReport simpleTimeline = new TimelineReport(simpleInput, version);
            TimelineReport boundaryTimeline = new TimelineReport(boundaryInput, version);

            CombineReport countReport = new CombineReport();
            countReport.AddReport("Simple", simpleTimeline);
            countReport.AddReport("Boundary", boundaryTimeline);
            countReport.GenerateReport(GetPath("reports\\manet_count.csv"));

            AgeReport simpleAge = new AgeReport(simpleInput, version);
            AgeReport boundaryAge = new AgeReport(boundaryInput, version);

            CombineReport ageReport = new CombineReport();
            ageReport.AddReport("Simple", simpleAge);
            ageReport.AddReport("Boundary", boundaryAge);
            ageReport.GenerateReport(GetPath("reports\\manet_age.csv"));
        }
        static void infrastructure()
        {
            Func<Request, string> version = new Func<Request, string>(r => string.Format("P:{0:F1} C:{1}", r.GetDouble("Probability"), r.GetString("PublishCount")));
            string simpleInput = GetPath("results\\ip-simple");
            string grouperInput = GetPath("results\\ip-grouper");

            TimelineReport simpleTimeline = new TimelineReport(simpleInput, version);
            TimelineReport grouperTimeline = new TimelineReport(grouperInput, version);

            CombineReport countReport = new CombineReport();
            countReport.AddReport("Simple", simpleTimeline);
            countReport.AddReport("Grouper", grouperTimeline);
            countReport.GenerateReport(GetPath("reports\\ip_count.csv"));

            AgeReport simpleAge = new AgeReport(simpleInput, version);
            AgeReport grouperAge = new AgeReport(grouperInput, version);

            CombineReport ageReport = new CombineReport();
            ageReport.AddReport("Simple", simpleAge);
            ageReport.AddReport("Grouper", grouperAge);
            ageReport.GenerateReport(GetPath("reports\\ip_age.csv"));
        }

        static void Main(string[] args)
        {
            //manet();
            infrastructure();

            Console.WriteLine("Done!");
            Console.ReadKey();
        }
    }
}
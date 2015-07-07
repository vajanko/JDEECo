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
        const string inputDir = "C:\\tmp\\jdeeco-reports";
        const string outputDir = "C:\\SkyDrive\\University\\10. semester\\Diplomová práce II\\reports\\";

        static string GetInPath(string file)
        {
            return Path.Combine(inputDir, file);
        }
        static string GetOutPath(string file)
        {
            return Path.Combine(outputDir, file);
        }
        static void CompareReports(string name1, IReport report1, string name2, IReport report2, string output)
        {
            CombineReport compareReport = new CombineReport();
            compareReport.AddReport(name1, report1);
            compareReport.AddReport(name2, report2);
            compareReport.GenerateReport(GetOutPath(output));
        }
        static void manet()
        {
            Func<Request, string> version = new Func<Request, string>(r => string.Format("P:{0:F1}", r.GetDouble("Probability")));
            string simpleInput = GetInPath("results\\manet-simple1");
            string boundaryInput = GetInPath("results\\manet-boundary1");

            TimelineReport simpleTimeline = new TimelineReport(simpleInput, version);
            TimelineReport boundaryTimeline = new TimelineReport(boundaryInput, version);
            CompareReports("Simple", simpleTimeline, "Boundary", boundaryTimeline, "manet_count.csv");

            AgeReport simpleAge = new AgeReport(simpleInput, version);
            AgeReport boundaryAge = new AgeReport(boundaryInput, version);
            CompareReports("Simple", simpleAge, "Boundary", boundaryAge, "manet_age.csv");

            TimelineReport simpleRcp = new TimelineReport(simpleInput, version, ActionType.RECV);
            TimelineReport boundaryRcp = new TimelineReport(boundaryInput, version, ActionType.RECV);
            CompareReports("Simple", simpleRcp, "Boundary", boundaryRcp, "manet_rcp.csv");

            //RebroadcastReport simpleRebr = new RebroadcastReport(simpleInput, version);
            //RebroadcastReport boundaryRebr = new RebroadcastReport(boundaryInput, version);
            //CompareReports("Simple", simpleRebr, "Boundary", boundaryRebr, "manet_rebr.csv");
        }
        static void infrastructure()
        {
            Func<Request, string> version = new Func<Request, string>(r => string.Format("P:{0:F1} C:{1}", r.GetDouble("Probability"), r.GetString("PublishCount")));
            string simpleInput = GetInPath("results\\ip-simple1");
            string grouperInput = GetInPath("results\\ip-grouper1");

            TimelineReport simpleTimeline = new TimelineReport(simpleInput, version);
            TimelineReport grouperTimeline = new TimelineReport(grouperInput, version);
            CompareReports("Simple", simpleTimeline, "Grouper", grouperTimeline, "ip_count.csv");

            AgeReport simpleAge = new AgeReport(simpleInput, version);
            AgeReport grouperAge = new AgeReport(grouperInput, version);
            CompareReports("Simple", simpleAge, "Grouper", grouperAge, "ip_age.csv");
        }

        static void Main(string[] args)
        {
            manet();
            //infrastructure();

            Console.WriteLine("Done!");
            //Console.ReadKey();
        }
    }
}
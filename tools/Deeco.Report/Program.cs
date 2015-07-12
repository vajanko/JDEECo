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
        string inputDir;
        string outputDir;

        string GetInPath(string file)
        {
            return Path.Combine(inputDir, file);
        }
        string GetOutPath(string file)
        {
            return Path.Combine(outputDir, file);
        }
        void CompareReports(string name1, IReport report1, string name2, IReport report2, string output)
        {
            CombineReport compareReport = new CombineReport();
            compareReport.AddReport(name1, report1);
            compareReport.AddReport(name2, report2);
            compareReport.GenerateReport(GetOutPath(output));
        }
        void manet()
        {
            Func<Request, string> version = new Func<Request, string>(r => string.Format("P:{0:F1}", r.GetDouble("Probability")));
            string simpleInput = GetInPath("manet-simple");
            string boundaryInput = GetInPath("manet-boundary");

            //TimelineReport simpleTimeline = new TimelineReport(simpleInput, version);
            //TimelineReport boundaryTimeline = new TimelineReport(boundaryInput, version);
            //CompareReports("Simple", simpleTimeline, "Boundary", boundaryTimeline, "manet_count.csv");

            //AgeReport simpleAge = new AgeReport(simpleInput, version);
            //AgeReport boundaryAge = new AgeReport(boundaryInput, version);
            //CompareReports("Simple", simpleAge, "Boundary", boundaryAge, "manet_age.csv");

            //TimelineReport simpleRcp = new TimelineReport(simpleInput, version, ActionType.RECV);
            //TimelineReport boundaryRcp = new TimelineReport(boundaryInput, version, ActionType.RECV);
            //CompareReports("Simple", simpleRcp, "Boundary", boundaryRcp, "manet_recp.csv");

            DeliveryReport simpleDel = new DeliveryReport(simpleInput, version);
            DeliveryReport boundaryDel = new DeliveryReport(boundaryInput, version);
            CompareReports("Simple", simpleDel, "Boundary", boundaryDel, "manet_del.csv");

            //DropReport simpleDrop = new DropReport(simpleInput);
            //DropReport boundaryDrop = new DropReport(boundaryInput);
            //CompareReports("Simple", simpleDrop, "Boundary", boundaryDrop, "manet_drop.csv");

            //RebroadcastReport simpleRebr = new RebroadcastReport(simpleInput, version);
            //RebroadcastReport boundaryRebr = new RebroadcastReport(boundaryInput, version);
            //CompareReports("Simple", simpleRebr, "Boundary", boundaryRebr, "manet_rebr.csv");
        }
        void infrastructure()
        {
            Func<Request, string> version = new Func<Request, string>(r => string.Format("P:{0:F1} C:{1}", r.GetDouble("Probability"), r.GetString("PublishCount")));
            string simpleInput = GetInPath("ip-simple2");
            string grouperInput = GetInPath("ip-grouper2");

            TimelineReport simpleTimeline = new TimelineReport(simpleInput, version);
            TimelineReport grouperTimeline = new TimelineReport(grouperInput, version);
            CompareReports("Simple", simpleTimeline, "Grouper", grouperTimeline, "ip_count.csv");

            AgeReport simpleAge = new AgeReport(simpleInput, version);
            AgeReport grouperAge = new AgeReport(grouperInput, version);
            //CompareReports("Simple", simpleAge, "Grouper", grouperAge, "ip_age.csv");
        }
        void pull()
        {
           

            //Func<Request, string> version = new Func<Request, string>(r => 
            //    string.Format("P:{0:F1} HP:{1}  L:{1} G:{2}", 
            //        r.GetDouble("Probability"), 
            //        r.GetInt("LocalTimeout"), 
            //        r.GetInt("GlobalTimeout"))
            //    );
            string pullInput = GetInPath("manet-pull");

            //List<string> files = new List<string>();
            //files.AddRange(Directory.EnumerateFiles(pullInput, "*.csv"));
            //foreach (string file in files)
            //{
            //    FileStream stream = File.OpenWrite(file);
            //    stream.Seek("Node;Time;Action;Type;ComponentId;".Length, SeekOrigin.Begin);
            //    var bytes = Encoding.ASCII.GetBytes("KN");
            //    stream.Write(bytes, 0, bytes.Length);
            //    stream.Close();
            //}

            PullReport pullAge = new PullReport(pullInput);
            pullAge.GenerateReport(GetOutPath("pull_age.csv"));

            CountReport pullCount = new CountReport(pullInput);
            pullCount.GenerateReport(GetOutPath("pull_count.csv"));
        }

        void generate()
        {
            //manet();
            //infrastructure();
            pull();
        }

        public Program()
            : this("C:\\tmp\\jdeeco-reports", "C:\\SkyDrive\\University\\10. semester\\Diplomová práce II\\reports\\") { }
        public Program(string inputDir, string outputDir)
        {
            this.inputDir = inputDir;
            this.outputDir = outputDir;
        }

        static void Main(string[] args)
        {
            Program prog = null;
            if (args.Length == 2)
            {
                prog = new Program(args[0], args[1]);
            }
            else
            {
                prog = new Program();
            }
            
            prog.generate();

            Console.WriteLine("Done!");
        }
    }
}
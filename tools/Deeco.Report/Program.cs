using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class Program
    {
        static void Main(string[] args)
        {
            Func<Request, string> ipSimple = new Func<Request, string>(r => string.Format("P:{0:F1} C:{1}", r.GetDouble("Probability"), r.GetString("PublishCount")));
            Func<Request, string> ipGrouper = new Func<Request,string>(r => string.Format("P:{0:F1} G:{1}", r.GetDouble("Probability"), r.GetString("Groupers")));
            var selected = ipGrouper;

            TimelineReport timeline = new TimelineReport("E:\\tmp\\logs\\", "E:\\tmp\\results\\ip-grouper.csv", selected);
            timeline.GenerateReport();

            Console.WriteLine("Done!");
            Console.ReadKey();
        }
    }
}

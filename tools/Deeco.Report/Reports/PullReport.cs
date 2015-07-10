using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    class PullReport : IReport
    {
        private string path;
        public PullReport(string inputPath)
        {
            this.path = inputPath;
        }
        private IEnumerable<string> getFiles()
        {
            List<string> files = new List<string>();
            foreach (string part in path.Split(';'))
            {
                if (File.Exists(part))
                    files.Add(part);
                else
                    files.AddRange(Directory.EnumerateFiles(part, "*.csv"));
            }

            return files;
        }



        public void GenerateReport(string outputFile)
        {
            File.Delete(outputFile);
            using (var writer = new StreamWriter(File.OpenWrite(outputFile)))
            {
                writer.WriteLine("HDPeriod;PLPeriod;Probability;LocalTimeout;GlobalTimeout;WBottom;Box1;Box2;Box3;WTop");

                //Parallel.ForEach(getFiles(), file =>
                foreach (var file in getFiles())
                {
                    Console.WriteLine(file);
                    var lines = File.ReadAllLines(file);
                    Request.InitHeader(lines.First());

                    var requests = lines.Skip(1)
                        .Select(ln => new Request(ln))
                        .Where(r => r.GetString("Type") == "KN" && 
                            r.GetString("Action") == "RECV" &&
                            r.GetString("IsSource") == "0");

                    writeFile(writer, requests);
                }
                //});
            }
        }
        private void writeFile(StreamWriter writer, IEnumerable<Request> requests)
        {
            Request first = requests.First();
            var hd = first.GetInt("HDPeriod");
            var pl = first.GetInt("PLPeriod");
            var prob = first.GetDouble("Probability");
            var loc = first.GetInt("LocalTimeout");
            var glob = first.GetInt("GlobalTimeout");

            var values = requests.Select(r => r.GetInt("Age")).OrderBy(i => i).ToList();
            var min = values[0];
            var max = values[values.Count - 1];
            var q1 = values[values.Count / 4];
            var median = values[values.Count / 2];
            var q3 = values[values.Count / 4 * 3];

            var bottom = q1 - min;
            var box1 = q1;
            var box2 = median - q1;
            var box3 = q3 - median;
            var top = max - q3;

            lock (writer)
            {
                writer.Write("{0};{1};{2};{3};{4};", hd, pl, prob, loc, glob);
                writer.WriteLine("{0};{1};{2};{3};{4}", bottom, box1, box2, box3, top);
            }
        }
    }
}

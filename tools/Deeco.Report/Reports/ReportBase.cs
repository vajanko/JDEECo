using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    abstract class ReportBase : IReport
    {
        private RequestSource source;
        private Func<Request, string> versionSelector;

        public ReportBase(string inputPath, Func<Request, string> versionSelector)
        {
            this.source = new RequestSource(inputPath);
            this.versionSelector = versionSelector;
        }

        protected IEnumerable<Request> GetRequests()
        {
            return this.source.GetRequests();
        }
        protected string GetVersion(Request req)
        {
            return this.versionSelector(req);
        }
        protected abstract void writeReport(StreamWriter writer);

        public void GenerateReport(string outputFile)
        {
            File.Delete(outputFile);
            using (var writer = new StreamWriter(File.OpenWrite(outputFile)))
            {
                writeReport(writer);
            }
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Deeco.Report
{
    interface IReport
    {
        void GenerateReport(string outputFile);
    }
}

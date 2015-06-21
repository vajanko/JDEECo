using Matsim;
using Matsim.Generator;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Matsim.Generator
{
    class PopulationGenerator : GeneratorBase
    {
        protected Graph network;

        protected IdProvider personIds = new IdProvider();

        public int PersonCount { get; private set; }
        public int ActivityCount { get; private set; }

        public PopulationGenerator(ConfigGenerator conf, Graph network, int personCount, int activityCount)
            : base(conf, "Templates/population.xml", "population.xml", "Templates/plans_v4.dtd")
        {
            this.network = network;
            this.PersonCount = personCount;
            this.ActivityCount = activityCount;
        }

        protected override void generateInternal(XDocument doc)
        {
            XElement plans = doc.Root;

            for (int i = 0; i < PersonCount; ++i)
            {
                int activities = random(ActivityCount - 2) + 2;     // at least two activities
                plans.Add(createPerson(activities));
            }
        }

        protected XElement createPerson(int activities)
        {
            XElement person = new XElement("person",
                personIds.CreateIdAttr(),
                createPlan(true, activities));

            return person;
        }
        protected XElement createPlan(bool selected, int activities)
        {
            XElement plan = new XElement("plan", new XAttribute("selected", selected ? "yes" : "no"));

            Edge edge;
            List<Edge> edges = new List<Edge>(network.GetEdges());

            List<TimeSpan> intervals = createRandomIntervals(config.Start, config.End, activities);
            foreach (TimeSpan endTime in intervals)
            {
                // random edge
                edge = edges[random(edges.Count)];

                // alternate activity with ..
                plan.Add(createActivity("w", edge.Id, edge.Position, endTime));
                // .. leg
                plan.Add(createLeg("pt"));
            }

            // remove last leg element
            plan.LastNode.Remove();

            return plan;
        }
        protected List<TimeSpan> createRandomIntervals(TimeSpan start, TimeSpan end, int count)
        {
            // list of random number which represents part of the complete simulation interval
            List<int> parts = new List<int>(count);
            parts.Add(random(2));
            for (int i = 1; i < count; ++i)
                parts.Add(random(100) + 20);
            // total simulation time
            int total = parts.Sum();
            double totalTime = (end - start).TotalSeconds;

            // simulation time is divided into instances
            double instantTime = totalTime / total;

            List<TimeSpan> intervals = new List<TimeSpan>();

            TimeSpan lastEnd = start;
            foreach (int p in parts)
            {
                lastEnd += TimeSpan.FromSeconds(p * instantTime);
                intervals.Add(lastEnd);
            }

            return intervals;
        }
        protected XElement createActivity(string type, string link, Position pos, TimeSpan endTime)
        {
            XElement activity = new XElement("act",
                new XAttribute("type", type),
                new XAttribute("link", link),
                new XAttribute("x", pos.X),
                new XAttribute("y", pos.Y),
                new XAttribute("end_time", endTime.ToString())
                );
            return activity;
        }
        protected XElement createLeg(string mode)
        {
            XElement leg = new XElement("leg", new XAttribute("mode", mode));

            return leg;
        }
    }
}

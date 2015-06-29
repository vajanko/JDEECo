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
    class ScheduleGenerator : GeneratorBase
    {
        class StopInfo
        {
            public string StopId { get; set; }
            public Edge Edge { get; set; }
        }

        protected IdProvider transitLineIds = new IdProvider();
        protected IdProvider transitRouteIds = new IdProvider();
        protected IdProvider departureIds = new IdProvider();

        protected VehicleGenerator vehicle;
        protected Graph network;

        public TimeSpan StopTime { get; set; }
        public int Transits { get; private set; }
        public int GroupSize { get; private set; }

        public ScheduleGenerator(ConfigGenerator conf, Graph network, VehicleGenerator vehicle, int transits, int groupSize)
            : base(conf, "Templates/schedule.xml", "schedule.xml", "Templates/transitSchedule_v1.dtd")
        {
            this.vehicle = vehicle;
            this.network = network;

            StopTime = TimeSpan.FromSeconds(3);
            Transits = transits;
            GroupSize = groupSize;
        }
        protected override void generateInternal(XDocument doc)
        {
            XElement schedule = doc.Root;

            List<Edge> edges = new List<Edge>(network.GetEdges());

            // a set of edges where is places a stop facility
            HashSet<Edge> stopEdges = new HashSet<Edge>();

            // total simulation time in seconds
            double totalTime = (config.End - config.Start).TotalSeconds;

            // generate schedule for each vehicle group - some of the vehicles has common
            // schedule so that the size of transit network will be smaller
            List<string> vehicleIds = new List<string>();
            foreach (var veh in vehicle.GetVehicles())
            {
                vehicleIds.Add(veh.GetId());
                if (vehicleIds.Count < GroupSize)
                    continue;

                var vehiclePath = createRandomPath(edges);

                // calculate how long the route will take
                double length = vehiclePath.Select(e => e.Length).Sum();
                double routeSeconds = vehiclePath.Select(e => e.Length / e.Freespeed + StopTime.TotalSeconds).Sum();
                TimeSpan routeTime = TimeSpan.FromSeconds(routeSeconds);

                // generate a transit line for particular vehicle
                schedule.Add(createTransitLine(ref vehiclePath, routeTime, vehicleIds));
                // notice that in the previous method call vehiclePath could be changed to only allow
                // such path which can be travelled during the simulation time
                // the path is shortened if it is too long, so the transit network will be smaller

                // remember all links through which some vehicle passes
                foreach (Edge e in vehiclePath)
                    stopEdges.Add(e);

                vehicleIds.Clear();
            }
            //var enumerator = vehicle.GetVehicles().GetEnumerator();
            //while (enumerator.MoveNext())
            //{
            //    var vehiclePath = createRandomPath(edges);

            //    // calculate how long the route will take
            //    double length = vehiclePath.Select(e => e.Length).Sum();
            //    double routeSeconds = vehiclePath.Select(e => e.Length / e.Freespeed + StopTime.TotalSeconds).Sum();
            //    TimeSpan routeTime = TimeSpan.FromSeconds(routeSeconds);

            //    List<string> vehicleIds = new List<string>();
            //    for (int c = 0; c < GroupSize; c++)
            //    {
            //        vehicleIds.Add(enumerator.Current.GetId());
            //        if (!enumerator.MoveNext())
            //            break;
                    
            //    }

            //    // generate a transit line for particular vehicle
            //    schedule.Add(createTransitLine(ref vehiclePath, routeTime, vehicleIds));
            //    // notice that in the previous method call vehiclePath could be changed to only allow
            //    // such path which can be travelled during the simulation time
            //    // the path is shortened if it is too long, so the transit network will be smaller

            //    // remember all links through which some vehicle passes
            //    foreach (Edge e in vehiclePath)
            //        stopEdges.Add(e);
            //}

            // generate stop facility at each link throught which same vehicle has passed
            schedule.AddFirst(createTransitStops(edges));
        }
        private List<Edge> createRandomPath(List<Edge> edges)
        {
            List<Edge> vehiclePath = new List<Edge>();

            // random start edge
            Edge start = edges[random(edges.Count)];
            Edge from = start;

            for (int i = 0; i < Transits; i++)
            {
                // next random edge - find shortest path to it and add it to the vehicle path
                int toId = random(edges.Count);
                Edge to = edges[toId];
                while (from.To == to.To)
                {
                    toId = (toId + 1) % edges.Count;
                    to = edges[toId];
                }

                IEnumerable<Edge> path = network.FindShortestPath(from.To, to.To);
                vehiclePath.AddRange(path);

                from = to;
            }

            // return to the source link - the route is a cycle
            IEnumerable<Edge> backPath = network.FindShortestPath(from.To, start.From);
            vehiclePath.AddRange(backPath);
            vehiclePath.Add(start);

            return vehiclePath;
        }

        protected XElement createTransitStops(IEnumerable<Edge> links)
        {
            XElement stops = new XElement("transitStops");

            foreach (Edge link in links)
            {
                stops.Add(createStopFacility(link.Position, link.Id));
            }

            return stops;
        }
        protected XElement createStopFacility(Position pos, string linkRefId)
        {
            XElement stop = new XElement("stopFacility",
                // use the same ID for stop facility as the link id
                new XAttribute("id", linkRefId),
                new XAttribute("x", pos.X),
                new XAttribute("y", pos.Y),
                new XAttribute("linkRefId", linkRefId)
                );

            return stop;
        }

        protected XElement createTransitLine(ref List<Edge> path, TimeSpan routeTime, IEnumerable<string> vehicleId)
        {
            XElement line = new XElement("transitLine",
                transitLineIds.CreateIdAttr(),
                createTransitRoute(ref path, routeTime, vehicleId));

            return line;
        }
        protected XElement createTransitRoute(ref List<Edge> path, TimeSpan routeTime, IEnumerable<string> vehicleId)
        {
            // we will modify the path if it cannot be passed during the simulation time
            List<Edge> myPath = new List<Edge>();

            XElement route = new XElement("transitRoute",
                transitRouteIds.CreateIdAttr(),
                new XElement("transportMode", "pt"),
                createRouteProfile(path, myPath),
                createRoute(myPath),
                createDepartures(routeTime, vehicleId));

            path = myPath;

            return route;
        }
        protected XElement createDepartures(TimeSpan routeTime, IEnumerable<string> vehicleIds)
        {
            XElement departures = new XElement("departures");

            foreach (string vehicleId in vehicleIds)
            {

                TimeSpan dept = config.Start + TimeSpan.FromSeconds(random(GroupSize * 2));
                TimeSpan step = routeTime + StopTime;

                while (dept < config.End)
                {
                    departures.Add(createDeparture(dept, vehicleId));
                    dept += step;
                }
            }

            return departures;
        }

        //protected XElement createTransitLine(IEnumerable<Edge> path, TimeSpan routeTime, string vehicleId)
        //{
        //    XElement line = new XElement("transitLine",
        //        transitLineIds.CreateIdAttr(),
        //        createTransitRoute(path, routeTime, vehicleId));

        //    return line;
        //}
        //protected XElement createTransitRoute(IEnumerable<Edge> path, TimeSpan routeTime, string vehicleId)
        //{
        //    // we will modify the path if it cannot be passed during the simulation time
        //    List<Edge> myPath = new List<Edge>();

        //    XElement route = new XElement("transitRoute",
        //        transitRouteIds.CreateIdAttr(),
        //        new XElement("transportMode", "pt"),
        //        createRouteProfile(path, myPath),
        //        createRoute(myPath),
        //        createDepartures(routeTime, vehicleId));

        //    return route;
        //}
        protected XElement createRouteProfile(IEnumerable<Edge> potentialPath, List<Edge> realPath)
        {
            XElement profile = new XElement("routeProfile");

            TimeSpan time = TimeSpan.Zero;
            int index = 0;
            int lastIndex = potentialPath.Count() - 1;
            // total simulation time
            TimeSpan simTime = config.End - config.Start;

            foreach (Edge link in potentialPath)
            {
                realPath.Add(link);

                // stop ID is the same as link ID
                string stopId = link.Id;
                XElement stop = new XElement("stop", new XAttribute("refId", stopId));

                if (index > 0)
                    stop.Add(new XAttribute("arrivalOffset", time.ToString()));

                if (index < lastIndex)
                    stop.Add(new XAttribute("departureOffset", (time + StopTime).ToString()));

                // calculate traveling time
                double travelSeconds = link.Length / link.Freespeed;
                time += TimeSpan.FromSeconds(travelSeconds);

                if (time > simTime)
                    break;

                profile.Add(stop);
                index++;
            }

            return profile;
        }
        protected XElement createRoute(IEnumerable<Edge> path)
        {
            XElement route = new XElement("route");

            foreach (Edge link in path)
            {
                route.Add(new XElement("link", new XAttribute("refId", link.Id)));
            }

            return route;
        }
        //protected XElement createDepartures(TimeSpan routeTime, string vehicleId)
        //{
        //    XElement departures = new XElement("departures");

        //    TimeSpan dept = config.Start;
        //    TimeSpan step = routeTime + StopTime;

        //    while (dept < config.End)
        //    {
        //        departures.Add(createDeparture(dept, vehicleId));
        //        dept += step;
        //    }

        //    return departures;
        //}
        protected XElement createDeparture(TimeSpan time, string vehicleId)
        {
            XElement departure = new XElement("departure",
                departureIds.CreateIdAttr(),
                new XAttribute("departureTime", time.ToString()),
                new XAttribute("vehicleRefId", vehicleId)
                );

            return departure;
        }
    }
}

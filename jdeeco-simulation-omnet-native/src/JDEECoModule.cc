#include "JDEECoModule.h"

#include <assert.h>
#include <limits>

#include "JDEECoRuntime.h"

JDEECoModule::JDEECoModule() {
	currentCallAtTime = std::numeric_limits<double>::min();
	currentCallAtMessage = NULL;
	initialized = false;
	runtime = NULL;
}

JDEECoModule::~JDEECoModule() {
}

void JDEECoModule::callAt(double absoluteTime) {
	//std::cout << "jDEECoCallAt: " << absoluteTime << " Begin" << std::endl;

	// Adjust possible past event time
	if (simTime().dbl() > absoluteTime) {
		std::cerr << "Time adjust for event in the past (" << absoluteTime
				<< " -> " << simTime().dbl() << ")" << std::endl;
		absoluteTime = simTime().dbl();
	}

	currentCallAtTime = absoluteTime;
	// TODO: Memory leak here?
	cMessage *msg = new cMessage(JDEECO_TIMER_MESSAGE);
	currentCallAtMessage = msg;
	registerCallbackAt(absoluteTime, msg);
	//std::cout << "jDEECoCallAt: " << getModuleId() << " Callback added: " << this->getModuleId() << " for " << absoluteTime << std::endl;

	//std::cout << "jDEECoCallAt: " << getModuleId() << " End" << std::endl;
}

void JDEECoModule::onHandleMessage(cMessage *msg, double rssi) {
	//std::cout << "jDEECoOnHandleMessage: " << getModuleId() << " Begin: " << std::endl;
	//std::cout << "jDEECoOnHandleMessage" << std::endl;
	if (runtime != NULL) {
		JNIEnv *env;
		//std::cout << "jDEECoOnHandleMessage: " << this->getModuleId() << " Before getting the environment" << std::endl;
		runtime->jvm->AttachCurrentThread((void **) &env, NULL);
		//std::cout << "jDEECoOnHandleMessage: " << this->getModuleId() << " Before getting jobject class" << std::endl;
		jclass cls = env->GetObjectClass(runtime->host);
		jmethodID mid;
		if (opp_strcmp(msg->getName(), JDEECO_TIMER_MESSAGE) == 0) {
			// compare in nanos
			if (((long) round(simTime().dbl() * 1000000))
					== ((long) round(currentCallAtTime * 1000000))) {
				//std::cout << "jDEECoOnHandleMessage: " << this->getModuleId() << " Before getting the \"at\" method reference" << std::endl;
				mid = env->GetMethodID(cls, "at", "(D)V");
				assert(mid != NULL);
				//std::cout << "jDEECoOnHandleMessage: " << this->getModuleId() << " Before calling the \"at\" method" << std::endl;
				env->CallVoidMethod(runtime->host, mid, currentCallAtTime);
			} else {
				//Ignore the message as it is not valid any longer.
			}
		} else if (opp_strcmp(msg->getName(), JDEECO_DATA_MESSAGE) == 0) {
			//std::cout << "jDEECoOnHandleMessage: " << this->getModuleId() << " Before getting the \"packetRecived\" method reference" << std::endl;
			EV << "OMNET++ (" << simTime() << ") : " << getModuleId()
					<< " received packet with ID = " << msg->getId() << endl;
			mid = env->GetMethodID(cls, "packetReceived", "([BD)V");
			if (mid == 0)
				return;
			JDEECoPacket *jPacket = check_and_cast<JDEECoPacket *>(msg);
			jbyte *buffer = new jbyte[jPacket->getDataArraySize()];
			for (unsigned int i = 0; i < jPacket->getDataArraySize(); i++)
				buffer[i] = jPacket->getData(i);
			jbyteArray jArray = env->NewByteArray(jPacket->getDataArraySize());
			if (jArray == NULL) {
				std::cerr << "onHandleMessage: " << this->getModuleId()
						<< " Cannot create new ByteArray object! Out of memory problem?"
						<< std::endl;
				return;
			}
			env->SetByteArrayRegion(jArray, 0, jPacket->getDataArraySize(),
					buffer);
			//std::cout << "jDEECoOnHandleMessage: " << this->getModuleId() << " Before calling the \"packetRecived\" method" << std::endl;
			env->CallVoidMethod(runtime->host, mid, jArray, rssi);
			//std::cout << "jDEECoOnHandleMessage: " << this->getModuleId() << " After calling the \"packetRecived\" method" << std::endl;
			env->DeleteLocalRef(jArray);
			//std::cout << "jDEECoOnHandleMessage: " << this->getModuleId() << " After deleting the array reference" << std::endl;
			delete[] buffer;
		}
		env->DeleteLocalRef(cls);
	}
	//std::cout << "jDEECoOnHandleMessage: " << this->jDEECoGetModuleId() << " End" << std::endl;
}

void JDEECoModule::initialize() {
	//std::cout << "initialize: " << this->getModuleId() << " Begin" << std::endl;
	//std::cout << "initialize: " << this->getModuleId() << " Initializing jDEECo module: " << this->getModuleId() << std::endl;
	if (!initialized) {
		runtime = JDEECoRuntime::findRuntime(getModuleId());

		assert(runtime != NULL);

		if (runtime->firstCallAt != std::numeric_limits<double>::min()) {
			//std::cout << "adding the first callback: " << this->getModuleId() << std::endl;
			callAt(runtime->firstCallAt);
		}

		jDEECoModules[getModuleId()] = this;
	}

	initialized = true;
	//std::cout << "initialize: " << this->getModuleId() << " End" << std::endl;
}

JDEECoModule* JDEECoModule::findModule(JNIEnv *env, jint id) {
	std::unordered_map<int, JDEECoModule*>::iterator it = jDEECoModules.find(id);

	if(it == jDEECoModules.end()) {
		return NULL;
	} else {
		return it->second;
	}
}

void JDEECoModule::clearAll() {
	jDEECoModules.clear();
}

std::unordered_map<int, JDEECoModule*> JDEECoModule::jDEECoModules;

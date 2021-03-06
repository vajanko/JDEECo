//
// Generated file, do not edit! Created by opp_msgc 4.3 from JDEECoPacket.msg.
//

#ifndef _JDEECOPACKET_M_H_
#define _JDEECOPACKET_M_H_

#include <omnetpp.h>

// opp_msgc version check
#define MSGC_VERSION 0x0403
#if (MSGC_VERSION!=OMNETPP_VERSION)
#    error Version mismatch! Probably this file was generated by an earlier version of opp_msgc: 'make clean' should help.
#endif



/**
 * Class generated from <tt>JDEECoPacket.msg</tt> by opp_msgc.
 * <pre>
 * packet JDEECoPacket {
 *      unsigned char data [];
 * }
 * </pre>
 */
class JDEECoPacket : public ::cPacket
{
  protected:
    unsigned char *data_var; // array ptr
    unsigned int data_arraysize;

  private:
    void copy(const JDEECoPacket& other);

  protected:
    // protected and unimplemented operator==(), to prevent accidental usage
    bool operator==(const JDEECoPacket&);

  public:
    JDEECoPacket(const char *name=NULL, int kind=0);
    JDEECoPacket(const JDEECoPacket& other);
    virtual ~JDEECoPacket();
    JDEECoPacket& operator=(const JDEECoPacket& other);
    virtual JDEECoPacket *dup() const {return new JDEECoPacket(*this);}
    virtual void parsimPack(cCommBuffer *b);
    virtual void parsimUnpack(cCommBuffer *b);

    // field getter/setter methods
    virtual void setDataArraySize(unsigned int size);
    virtual unsigned int getDataArraySize() const;
    virtual unsigned char getData(unsigned int k) const;
    virtual void setData(unsigned int k, unsigned char data);
};

inline void doPacking(cCommBuffer *b, JDEECoPacket& obj) {obj.parsimPack(b);}
inline void doUnpacking(cCommBuffer *b, JDEECoPacket& obj) {obj.parsimUnpack(b);}


#endif // _JDEECOPACKET_M_H_

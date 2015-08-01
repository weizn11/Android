#ifndef GLOBAL_H_INCLUDED
#define GLOBAL_H_INCLUDED

#include <winsock2.h>

#define SERVER_LISTEN_PORT 9999       //�����������˿�
#define MAX_SIZE 1024

extern struct _IO_OPERATION_DATA_;

typedef struct _OPPONENT_INFORMATION_
{
    SOCKET Socket;
    int PlayGameFlag;//0:û�н�����Ϸ��1����Ϸ�ѿ�ʼ��
    struct _IO_OPERATION_DATA_ *IO_Data;
}OPPONENT_INFO;

typedef struct _IO_OPERATION_DATA_
{
    //�ص��ṹ
    WSAOVERLAPPED overlap;
    struct sockaddr_in ClientAddress;
    WSABUF WSABuffer;
    char RecvBuffer[MAX_SIZE+1];
    char *ClientPacket;
    int ClientPacketSize;
    int RandomMatchFlag;        //0:û����ƥ������С�1���������ƥ������С�
    int PlayGameFlag;
    int PlayingChessFlag;       //�ֵ����������ı�ʶ
    unsigned long LastTime;
    DWORD RecvSize;
    DWORD flag;
    OPPONENT_INFO Opponent;
}IO_OPERATION_DATA;

typedef struct _IO_OPERATION_DATA_LIST_NODE_
{
    IO_OPERATION_DATA *Array[MAXIMUM_WAIT_OBJECTS];
    struct _IO_OPERATION_DATA_LIST_NODE_ *next;
}IO_OPERATION_DATA_NODE;

typedef struct _CLIENT_SOCKET_LIST_NODE_
{
    SOCKET SocketArray[MAXIMUM_WAIT_OBJECTS];
    WSAEVENT EventArray[MAXIMUM_WAIT_OBJECTS];
    struct _CLIENT_SOCKET_LIST_NODE_ *next;
}CLIENT_SOCKET_NODE;

typedef struct _CLIENT_PACKET_
{
    int GameMode;           //1�����Ѷ��ġ�2��������ġ�0�����ı䵱ǰ��Ϸģʽ��-1���˳���ǰ��Ϸģʽ
    int GameType;           //2�������ӡ�4���Ŀ��ӡ�5�������
    int ChessCoord[4];      //ǰ����Ԫ���Ǿ������ַ��������Ԫ�����������ַ��
} CLIENT_PACKET;

typedef struct _SERVER_PACKET_
{
    int play;
    int ChessCoord[4];
}SERVER_PACKET;

typedef struct _DISPOSE_CLIENT_PACKET_PARAMETER_
{
    SOCKET ClientSocket;
    IO_OPERATION_DATA *IO_Data;
    CLIENT_PACKET Packet;
}DISPOSE_CLIENT_PACKET_PARAMETER;

typedef struct _RANDOM_MATCH_NODE_
{
    int GameType;
    SOCKET PlayerSocket;
    IO_OPERATION_DATA *IO_Operation_Data_Pointer;
    struct _RANDOM_MATCH_NODE_ *next;
}RANDOM_MATCH_NODE;

#endif // GLOBAL_H_INCLUDED








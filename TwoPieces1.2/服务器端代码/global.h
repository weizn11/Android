#ifndef GLOBAL_H_INCLUDED
#define GLOBAL_H_INCLUDED

#include <winsock2.h>

#define SERVER_LISTEN_PORT 9999       //服务器监听端口
#define MAX_SIZE 1024

extern struct _IO_OPERATION_DATA_;

typedef struct _OPPONENT_INFORMATION_
{
    SOCKET Socket;
    int PlayGameFlag;//0:没有进行游戏。1：游戏已开始。
    struct _IO_OPERATION_DATA_ *IO_Data;
}OPPONENT_INFO;

typedef struct _IO_OPERATION_DATA_
{
    //重叠结构
    WSAOVERLAPPED overlap;
    struct sockaddr_in ClientAddress;
    WSABUF WSABuffer;
    char RecvBuffer[MAX_SIZE+1];
    char *ClientPacket;
    int ClientPacketSize;
    int RandomMatchFlag;        //0:没有在匹配队列中。1：正在随机匹配队列中。
    int PlayGameFlag;
    int PlayingChessFlag;       //轮到该玩家下棋的标识
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
    int GameMode;           //1：好友对弈。2：随机对弈。0：不改变当前游戏模式。-1：退出当前游戏模式
    int GameType;           //2：两块子。4：四块子。5：五块子
    int ChessCoord[4];      //前两个元素是旧坐标地址，后两个元素是新坐标地址。
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








#include "com_example_jni_ChessServerJNI.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <unistd.h>

#define SERVER_DOMAIN "game.weizn.net"
#define SERVER_PORT 9999
#define MAX_SIZE 1024
#define INVALID_SOCKET -1
#define SOCKET int

typedef struct _SERVER_PACKET_ {
	int play;
	int ChessCoord[4];
} SERVER_PACKET;

typedef struct _CLIENT_PACKET_ {
	int GameMode;           //1：好友对弈。2：随机对弈。0：不改变当前游戏模式。-1：退出当前游戏模式
	int GameType;           //2：两块子。4：四块子。5：五块子
	int ChessCoord[4];      //前两个元素是旧坐标地址，后两个元素是新坐标地址。
} CLIENT_PACKET;

SOCKET ServerSocket = INVALID_SOCKET;

int DNS_query(char *domain, char *ip) {
	char **tmp = NULL;
	struct hostent *ht = NULL;
	struct in_addr addr;

	memset(ip, NULL, 16);

	ht = gethostbyname(domain);
	if (ht == NULL)
		return -1;

	tmp = ht->h_addr_list;
	if (*tmp == NULL)
		return -1;
	addr.s_addr = *((unsigned int *) *tmp);
	strcat(ip, inet_ntoa(addr));

	return 0;
}

int connect_to_server() {
	char ServerIP[16];
	struct sockaddr_in ServerAddress;

	memset(&ServerAddress, NULL, sizeof(struct sockaddr_in));
	if (DNS_query(SERVER_DOMAIN, ServerIP) != 0)
		return -1;
	ServerAddress.sin_addr.s_addr = inet_addr(ServerIP);
	ServerAddress.sin_family = AF_INET;
	ServerAddress.sin_port = htons(SERVER_PORT);

	if ((ServerSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP))
			== INVALID_SOCKET)
		return -2;
	if (connect(ServerSocket, (struct sockaddr *) &ServerAddress,
			sizeof(struct sockaddr_in)) != 0) {
		close(ServerSocket);
		ServerSocket = INVALID_SOCKET;
		return -3;
	}

	return 0;
}

int send_packet_to_server(int GameMode, int GameType, int ChessCoord[4]) {
	int i;
	CLIENT_PACKET Packet;

	memset(&Packet, NULL, sizeof(CLIENT_PACKET));

	if (ServerSocket == INVALID_SOCKET)
		if (connect_to_server() != 0)
			return -1;              //连接至服务器失败

	Packet.GameMode = GameMode;
	Packet.GameType = GameType;
	for (i = 0; i < 4; i++)
		Packet.ChessCoord[i] = ChessCoord[i];

	if (send(ServerSocket, (char *) &Packet, sizeof(CLIENT_PACKET), 0) <= 0)
		return -2;                 //与服务器断开连接

	return 0;
}

int *recv_packet_from_server()
{
    int i,RecvSize,DataSize;
    int *coord=NULL;
    int SocketTimeOut=10000;     //设置套接字超时时间为10秒
    char RecvBuffer[MAX_SIZE+1];
    char *DataBuffer=NULL;
    SERVER_PACKET *Packet=NULL;

    coord=(int *)malloc(5*sizeof(int));
    DataBuffer=(char *)malloc(sizeof(SERVER_PACKET)+1);
    if(coord==NULL || DataBuffer==NULL) return NULL;
    memset(coord,NULL,5*sizeof(int));
    memset(DataBuffer,NULL,sizeof(SERVER_PACKET)+1);
    Packet=(SERVER_PACKET *)DataBuffer;

    setsockopt(ServerSocket,SOL_SOCKET,SO_RCVTIMEO,(char *)&SocketTimeOut,sizeof(int));
    for(RecvSize=DataSize=0; DataSize<sizeof(SERVER_PACKET);)
    {
        RecvSize=recv(ServerSocket,RecvBuffer,sizeof(RecvBuffer)-1,0);
        if(RecvSize<=0)
        {
            free(DataBuffer);
            free(coord);
            return  NULL;
        }
        if(RecvSize+DataSize<=sizeof(SERVER_PACKET))
            memcpy(&DataBuffer[DataSize],RecvBuffer,RecvSize);
        DataSize=+RecvSize;
    }

    coord[0]=Packet->play;
    for(i=0; i<4; i++)
        coord[i+1]=Packet->ChessCoord[i];
    free(DataBuffer);

    return coord;
}

int disconnect() {
	close(ServerSocket);
	ServerSocket = INVALID_SOCKET;
}

JNIEXPORT jint JNICALL Java_com_example_jni_ChessServerJNI_send_1packet_1to_1server(
		JNIEnv *env, jobject obj, jint m, jint n, jintArray array) {
	int a[4];
	int size = env->GetArrayLength(array);
	jint *coldata = env->GetIntArrayElements((jintArray) array, 0);
	for (int i = 0; i < size && i < 4; i++) {
		a[i] = coldata[i];
	}
	env->ReleaseIntArrayElements((jintArray) array, coldata, 0);
	return send_packet_to_server(m, n, a);
}

JNIEXPORT jintArray JNICALL Java_com_example_jni_ChessServerJNI_recv_1packet_1from_1server(
		JNIEnv *env, jobject obj) {
	int *p;
	int a[5];
	for (int j = 0; j < 5; ++j) {
		a[j] = 0;
	}
	jintArray iarr = env->NewIntArray(6);
	p = recv_packet_from_server();
	if (p != NULL) {
		for (int q = 0; q < 5; q++) {
			a[q] = *(p + q);
		}
		free(p);
	}else{
		a[0] = -1;
	}
	env->SetIntArrayRegion(iarr, 0, 5, a);
	return iarr;
}

JNIEXPORT void JNICALL Java_com_example_jni_ChessServerJNI_disconnect
(JNIEnv *env, jobject obj) {
	disconnect();
}

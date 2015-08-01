#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>
#include <winsock2.h>
#include <time.h>
#include <conio.h>

#include "global.h"
#include "recv.h"
#include "analysis.h"

CLIENT_SOCKET_NODE *Client_Socket_Header=NULL;
IO_OPERATION_DATA_NODE *IO_Operation_Data_Header=NULL;
unsigned long int TotalConnection=0,ListenThreads=0;
CRITICAL_SECTION CS_ListenThreads,CS_ClientList,CS_RandomMatch;;

DWORD WINAPI listen_client(LPVOID Parameter)
{
    WSADATA wsa;
    SOCKET ServerSocket=INVALID_SOCKET,ClientSocket=INVALID_SOCKET;
    struct sockaddr_in ServerAddress,ClientAddress;
    int AddressLength,index,*ListenThreadParameter=NULL;
    CLIENT_SOCKET_NODE *Client_Socket_Node_Pointer=NULL;
    IO_OPERATION_DATA_NODE *IO_Operation_Node_Pointer=NULL;

    memset(&wsa,NULL,sizeof(WSADATA));
    memset(&ServerAddress,NULL,sizeof(struct sockaddr_in));

    ServerAddress.sin_family=AF_INET;
    ServerAddress.sin_addr.s_addr=INADDR_ANY;
    ServerAddress.sin_port=htons(SERVER_LISTEN_PORT);

    if(WSAStartup(MAKEWORD(2,2),&wsa)!=0)
    {
        printf("Initialization failed!\n");
        getch();
        exit(-1);
    }

    if((ServerSocket=socket(AF_INET,SOCK_STREAM,IPPROTO_TCP))==INVALID_SOCKET)
    {
        printf("Create socket failed!\n");
        getch();
        exit(-1);
    }

    if(bind(ServerSocket,(struct sockaddr *)&ServerAddress,sizeof(struct sockaddr_in))!=0)
    {
        printf("Bind local address failed\n");
        getch();
        exit(-1);
    }

    if(listen(ServerSocket,SOMAXCONN)!=0)
    {
        printf("Listen local port failed\n");
        getch();
        exit(-1);
    }
    printf("Listening...\n");
    while(1)
    {
        AddressLength=sizeof(struct sockaddr_in);
        memset(&ClientAddress,NULL,sizeof(struct sockaddr_in));
        ClientSocket=accept(ServerSocket,(struct sockaddr *)&ClientAddress,&AddressLength);
        if(ClientSocket==INVALID_SOCKET) continue;

        EnterCriticalSection(&CS_ClientList);
        if(TotalConnection==0)
        {
            Client_Socket_Header=(CLIENT_SOCKET_NODE *)malloc(sizeof(CLIENT_SOCKET_NODE));
            IO_Operation_Data_Header=(IO_OPERATION_DATA_NODE *)malloc(sizeof(IO_OPERATION_DATA_NODE));
            if(Client_Socket_Header==NULL || IO_Operation_Data_Header==NULL)
            {
                printf("malloc() error!\n");
                LeaveCriticalSection(&CS_ClientList);
                return -1;
            }
            memset(Client_Socket_Header,NULL,sizeof(CLIENT_SOCKET_NODE));
            memset(IO_Operation_Data_Header,NULL,sizeof(IO_OPERATION_DATA_NODE));
        }
        else
        {
            if(TotalConnection%MAXIMUM_WAIT_OBJECTS==0)
            {
                //所有线程的任务已满
                for(Client_Socket_Node_Pointer=Client_Socket_Header; Client_Socket_Node_Pointer->next!=NULL; \
                        Client_Socket_Node_Pointer=Client_Socket_Node_Pointer->next);
                for(IO_Operation_Node_Pointer=IO_Operation_Data_Header; IO_Operation_Node_Pointer->next!=NULL; \
                        IO_Operation_Node_Pointer=IO_Operation_Node_Pointer->next);
                IO_Operation_Node_Pointer->next=(IO_OPERATION_DATA_NODE *)malloc(sizeof(IO_OPERATION_DATA_NODE));
                Client_Socket_Node_Pointer->next=(CLIENT_SOCKET_NODE *)malloc(sizeof(CLIENT_SOCKET_NODE));
                if(IO_Operation_Node_Pointer->next==NULL || Client_Socket_Node_Pointer->next==NULL)
                {
                    printf("malloc() error!\n");
                    LeaveCriticalSection(&CS_ClientList);
                    return -1;
                }
                memset(IO_Operation_Node_Pointer->next,NULL,sizeof(IO_OPERATION_DATA_NODE));
                memset(Client_Socket_Node_Pointer->next,NULL,sizeof(CLIENT_SOCKET_NODE));
            }
        }
        Client_Socket_Node_Pointer=Client_Socket_Header;
        IO_Operation_Node_Pointer=IO_Operation_Data_Header;
        while(Client_Socket_Node_Pointer->next!=NULL)
        {
            Client_Socket_Node_Pointer=Client_Socket_Node_Pointer->next;
            IO_Operation_Node_Pointer=IO_Operation_Node_Pointer->next;
        }
        index=TotalConnection%MAXIMUM_WAIT_OBJECTS;
        IO_Operation_Node_Pointer->Array[index]=(IO_OPERATION_DATA *)malloc(sizeof(IO_OPERATION_DATA));
        if(IO_Operation_Node_Pointer->Array[index]==NULL)
        {
            printf("malloc() error!\n");
            LeaveCriticalSection(&CS_ClientList);
            return -1;
        }
        memset(IO_Operation_Node_Pointer->Array[index],NULL,sizeof(IO_OPERATION_DATA));
        Client_Socket_Node_Pointer->SocketArray[index]=ClientSocket;
        IO_Operation_Node_Pointer->Array[index]->overlap.hEvent=Client_Socket_Node_Pointer->EventArray[index]=WSACreateEvent();
        IO_Operation_Node_Pointer->Array[index]->WSABuffer.buf=IO_Operation_Node_Pointer->Array[index]->RecvBuffer;
        IO_Operation_Node_Pointer->Array[index]->WSABuffer.len=MAX_SIZE;
        IO_Operation_Node_Pointer->Array[index]->ClientAddress=ClientAddress;
        WSARecv(ClientSocket,&IO_Operation_Node_Pointer->Array[index]->WSABuffer,1,&IO_Operation_Node_Pointer->Array[index]->RecvSize,\
                &IO_Operation_Node_Pointer->Array[index]->flag,&IO_Operation_Node_Pointer->Array[index]->overlap,NULL);
        TotalConnection++;
        if((TotalConnection-1)%MAXIMUM_WAIT_OBJECTS==0)
        {
            //创建新的监听线程
            CloseHandle(CreateThread(NULL,0,recv_message_from_client,NULL,0,NULL));
        }
        printf("Client \"%s\" online.\n",inet_ntoa(ClientAddress.sin_addr));
        printf("TotalConnect:%d\n",TotalConnection);
        LeaveCriticalSection(&CS_ClientList);
    }

    return 0;
}

int main(int argc,char *argv[])
{
    InitializeCriticalSection(&CS_ListenThreads);
    InitializeCriticalSection(&CS_ClientList);
    InitializeCriticalSection(&CS_RandomMatch);

    CloseHandle(CreateThread(NULL,0,listen_client,NULL,0,NULL));
    CloseHandle(CreateThread(NULL,0,check_timeout,NULL,0,NULL));

    while(1) Sleep(1000);

    return 0;
}










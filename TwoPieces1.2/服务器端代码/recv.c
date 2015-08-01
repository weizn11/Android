#include "global.h"
#include "recv.h"
#include "analysis.h"

extern CLIENT_SOCKET_NODE *Client_Socket_Header;
extern IO_OPERATION_DATA_NODE *IO_Operation_Data_Header;
extern unsigned long int TotalConnection,ListenThreads;
extern CRITICAL_SECTION CS_ListenThreads,CS_ClientList;
extern CRITICAL_SECTION CS_RandomMatch;
extern RANDOM_MATCH_NODE *Random_Match_Header;

int cleanup_client_connection(int ThreadIndex,int EventIndex)
{
    int i=ThreadIndex-1;
    CLIENT_SOCKET_NODE *Client_Socket_Node_Pointer=NULL,*Client_Socket_Last_Pointer=NULL;
    IO_OPERATION_DATA_NODE *IO_Operation_Node_Pointer=NULL,*IO_Operation_Last_Pointer=NULL;
    RANDOM_MATCH_NODE *Random_Match_Node_Pointer=NULL,*Random_Match_Parent_Pointer=NULL;

    EnterCriticalSection(&CS_ClientList);
    Client_Socket_Node_Pointer=Client_Socket_Header;
    IO_Operation_Node_Pointer=IO_Operation_Data_Header;
    while(i)
    {
        Client_Socket_Node_Pointer=Client_Socket_Node_Pointer->next;
        IO_Operation_Node_Pointer=IO_Operation_Node_Pointer->next;
        i--;
    }
    printf("Client \"%s\" logged off.\n",inet_ntoa(IO_Operation_Node_Pointer->Array[EventIndex]->ClientAddress.sin_addr));
    if(IO_Operation_Node_Pointer->Array[EventIndex]->PlayGameFlag)
    {
        //当前客户端在游戏中断开连接
        IO_Operation_Node_Pointer->Array[EventIndex]->Opponent.IO_Data->Opponent.PlayGameFlag=0;
        IO_Operation_Node_Pointer->Array[EventIndex]->Opponent.IO_Data->Opponent.Socket=INVALID_SOCKET;
    }
    if(IO_Operation_Node_Pointer->Array[EventIndex]->RandomMatchFlag)
    {
        //当前客户端在匹配队列中
        EnterCriticalSection(&CS_RandomMatch);
        for(Random_Match_Node_Pointer=Random_Match_Header,Random_Match_Parent_Pointer=NULL; Random_Match_Node_Pointer!=NULL && \
                Random_Match_Node_Pointer->PlayerSocket!=Client_Socket_Node_Pointer->SocketArray[EventIndex];)
        {
            Random_Match_Parent_Pointer=Random_Match_Node_Pointer;
            Random_Match_Node_Pointer=Random_Match_Node_Pointer->next;
        }
        if(Random_Match_Node_Pointer!=NULL)
        {
            if(Random_Match_Parent_Pointer!=NULL)
            {
                Random_Match_Parent_Pointer->next=Random_Match_Node_Pointer->next;
                free(Random_Match_Node_Pointer);
            }
            else
            {
                Random_Match_Header=Random_Match_Node_Pointer->next;
                free(Random_Match_Node_Pointer);
            }
        }
        LeaveCriticalSection(&CS_RandomMatch);
    }

    closesocket(Client_Socket_Node_Pointer->SocketArray[EventIndex]);
    WSACloseEvent(Client_Socket_Node_Pointer->EventArray[EventIndex]);
    free(IO_Operation_Node_Pointer->Array[EventIndex]);
    IO_Operation_Node_Pointer->Array[EventIndex]=NULL;

    Client_Socket_Last_Pointer=Client_Socket_Node_Pointer;
    IO_Operation_Last_Pointer=IO_Operation_Node_Pointer;
    while(Client_Socket_Last_Pointer->next!=NULL)
    {
        Client_Socket_Last_Pointer=Client_Socket_Last_Pointer->next;
        IO_Operation_Last_Pointer=IO_Operation_Last_Pointer->next;
    }

    i=(TotalConnection-1)%MAXIMUM_WAIT_OBJECTS;
    Client_Socket_Node_Pointer->SocketArray[EventIndex]=Client_Socket_Last_Pointer->SocketArray[i];
    Client_Socket_Node_Pointer->EventArray[EventIndex]=Client_Socket_Last_Pointer->EventArray[i];
    IO_Operation_Node_Pointer->Array[EventIndex]=IO_Operation_Last_Pointer->Array[i];
    IO_Operation_Last_Pointer->Array[i]=NULL;
    if(i==0)
    {
        Client_Socket_Node_Pointer=Client_Socket_Header;
        IO_Operation_Node_Pointer=IO_Operation_Data_Header;
        while(Client_Socket_Node_Pointer->next!=NULL && Client_Socket_Node_Pointer->next!=Client_Socket_Last_Pointer)
        {
            Client_Socket_Node_Pointer=Client_Socket_Node_Pointer->next;
            IO_Operation_Node_Pointer=IO_Operation_Node_Pointer->next;
        }
        if(Client_Socket_Node_Pointer->next==NULL)
        {
            free(Client_Socket_Header);
            free(IO_Operation_Data_Header);
            Client_Socket_Header=NULL;
            IO_Operation_Data_Header=NULL;
        }
        else
        {
            free(Client_Socket_Node_Pointer->next);
            free(IO_Operation_Node_Pointer->next);
            Client_Socket_Node_Pointer->next=NULL;
            IO_Operation_Node_Pointer->next=NULL;
        }
    }
    TotalConnection--;
    printf("TotalConnect:%d\n",TotalConnection);
    LeaveCriticalSection(&CS_ClientList);

    return 0;
}

DWORD WINAPI recv_message_from_client(LPVOID Parameter)
{
    int i,ThreadIndex,ClientCount,index;
    CLIENT_SOCKET_NODE *Client_Socket_Node_Pointer=NULL;
    IO_OPERATION_DATA_NODE *IO_Operation_Node_Pointer=NULL;
    DISPOSE_CLIENT_PACKET_PARAMETER *Dispose_Parameter=NULL;

    EnterCriticalSection(&CS_ListenThreads);
    ListenThreads++;
    i=ThreadIndex=ListenThreads;
    LeaveCriticalSection(&CS_ListenThreads);

    Client_Socket_Node_Pointer=Client_Socket_Header;
    IO_Operation_Node_Pointer=IO_Operation_Data_Header;
    i--;
    while(i)
    {
        Client_Socket_Node_Pointer=Client_Socket_Node_Pointer->next;
        IO_Operation_Node_Pointer=IO_Operation_Node_Pointer->next;
        i--;
    }
    while(1)
    {
        if(ListenThreads>ThreadIndex)
            ClientCount=MAXIMUM_WAIT_OBJECTS;
        else
            ClientCount=TotalConnection-(ThreadIndex-1)*MAXIMUM_WAIT_OBJECTS;
        if(ClientCount<=0)
        {
            //线程退出
            EnterCriticalSection(&CS_ListenThreads);
            ListenThreads--;
            LeaveCriticalSection(&CS_ListenThreads);
            return 0;
        }

        index=WSAWaitForMultipleEvents(ClientCount,Client_Socket_Node_Pointer->EventArray,FALSE,1,FALSE);
        if(index==WSA_WAIT_FAILED || index==WSA_WAIT_TIMEOUT)
        {
            if(ListenThreads>ThreadIndex)
                ClientCount=MAXIMUM_WAIT_OBJECTS;
            else
                ClientCount=TotalConnection-(ThreadIndex-1)*MAXIMUM_WAIT_OBJECTS;

            if(ClientCount<=0)
            {
                //线程退出
                EnterCriticalSection(&CS_ListenThreads);
                ListenThreads--;
                LeaveCriticalSection(&CS_ListenThreads);
                return 0;
            }
            continue;
        }

        index-=WSA_WAIT_EVENT_0;    //获取事件发生的对应索引
        WSAResetEvent(Client_Socket_Node_Pointer->EventArray[index]);    //重置事件
        WSAGetOverlappedResult(Client_Socket_Node_Pointer->SocketArray[index],&IO_Operation_Node_Pointer->Array[index]->overlap,\
                               &IO_Operation_Node_Pointer->Array[index]->RecvSize,TRUE,&IO_Operation_Node_Pointer->Array[index]->flag);
        if(IO_Operation_Node_Pointer->Array[index]->RecvSize<=0)
        {
            //客户端退出
            cleanup_client_connection(ThreadIndex,index);
            continue;
        }

        //处理RecvBuffer
        if(IO_Operation_Node_Pointer->Array[index]->ClientPacketSize+\
                IO_Operation_Node_Pointer->Array[index]->RecvSize>sizeof(CLIENT_PACKET))
        {
            //接收到错误数据包
            printf("接收到越界数据包。(Client:%s)\n",inet_ntoa(IO_Operation_Node_Pointer->Array[index]->ClientAddress.sin_addr));
            cleanup_client_connection(ThreadIndex,index);
            continue;
        }
        if(IO_Operation_Node_Pointer->Array[index]->ClientPacket==NULL)
        {
            IO_Operation_Node_Pointer->Array[index]->ClientPacket=(char *)malloc(sizeof(CLIENT_PACKET)+1);
            if(IO_Operation_Node_Pointer->Array[index]->ClientPacket==NULL)
            {
                printf("malloc() error!\n");
                return -1;
            }
            memset(IO_Operation_Node_Pointer->Array[index]->ClientPacket,NULL,sizeof(CLIENT_PACKET)+1);
            memcpy(IO_Operation_Node_Pointer->Array[index]->ClientPacket,IO_Operation_Node_Pointer->Array[index]->RecvBuffer,\
                   IO_Operation_Node_Pointer->Array[index]->RecvSize);
            IO_Operation_Node_Pointer->Array[index]->ClientPacketSize+=IO_Operation_Node_Pointer->Array[index]->RecvSize;
        }
        else
        {
            memcpy(&IO_Operation_Node_Pointer->Array[index]->ClientPacket[IO_Operation_Node_Pointer->Array[index]->ClientPacketSize],\
                   IO_Operation_Node_Pointer->Array[index]->RecvBuffer,IO_Operation_Node_Pointer->Array[index]->RecvSize);
            IO_Operation_Node_Pointer->Array[index]->ClientPacketSize+=IO_Operation_Node_Pointer->Array[index]->RecvSize;
        }
        if(IO_Operation_Node_Pointer->Array[index]->ClientPacketSize==sizeof(CLIENT_PACKET))
        {
            //数据包接收完毕
            Dispose_Parameter=(DISPOSE_CLIENT_PACKET_PARAMETER *)malloc(sizeof(DISPOSE_CLIENT_PACKET_PARAMETER));
            if(Dispose_Parameter==NULL)
            {
                printf("malloc() error!\n");
                getch();
                exit(-1);
            }
            memset(Dispose_Parameter,NULL,sizeof(DISPOSE_CLIENT_PACKET_PARAMETER));
            Dispose_Parameter->ClientSocket=Client_Socket_Node_Pointer->SocketArray[index];
            Dispose_Parameter->IO_Data=IO_Operation_Node_Pointer->Array[index];
            memcpy((char *)&Dispose_Parameter->Packet,IO_Operation_Node_Pointer->Array[index]->ClientPacket,sizeof(CLIENT_PACKET));
            free(IO_Operation_Node_Pointer->Array[index]->ClientPacket);
            IO_Operation_Node_Pointer->Array[index]->ClientPacket=NULL;
            IO_Operation_Node_Pointer->Array[index]->ClientPacketSize=0;
            CloseHandle(CreateThread(NULL,0,dispose_client_packet,(LPVOID)Dispose_Parameter,0,NULL));
        }

        memset(&IO_Operation_Node_Pointer->Array[index]->RecvBuffer,NULL,MAX_SIZE+1);
        WSARecv(Client_Socket_Node_Pointer->SocketArray[index],&IO_Operation_Node_Pointer->Array[index]->WSABuffer,1,\
                &IO_Operation_Node_Pointer->Array[index]->RecvSize,&IO_Operation_Node_Pointer->Array[index]->flag,\
                &IO_Operation_Node_Pointer->Array[index]->overlap,NULL);
    }
    return 0;
}









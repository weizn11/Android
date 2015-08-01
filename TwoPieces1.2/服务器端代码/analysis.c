#include "global.h"
#include "analysis.h"

RANDOM_MATCH_NODE *Random_Match_Header=NULL;
extern CRITICAL_SECTION CS_RandomMatch,CS_ClientList;
extern IO_OPERATION_DATA_NODE *IO_Operation_Data_Header;
extern CLIENT_SOCKET_NODE *Client_Socket_Header;

DWORD WINAPI check_timeout(LPVOID Parameter)
{
    int i;
    unsigned long NowTime;
    SERVER_PACKET Packet;
    IO_OPERATION_DATA_NODE *IO_Operation_Node_Pointer=NULL;
    CLIENT_SOCKET_NODE *Client_Socket_Node_Pointer=NULL;

    while(1)
    {
        if(IO_Operation_Data_Header==NULL)
        {
            Sleep(100);
            continue;
        }
        EnterCriticalSection(&CS_ClientList);
        NowTime=time(NULL);
        for(IO_Operation_Node_Pointer=IO_Operation_Data_Header,Client_Socket_Node_Pointer=Client_Socket_Header; IO_Operation_Node_Pointer!=NULL; \
                IO_Operation_Node_Pointer=IO_Operation_Node_Pointer->next,Client_Socket_Node_Pointer=Client_Socket_Node_Pointer->next)
        {
            for(i=0; i<MAXIMUM_WAIT_OBJECTS && IO_Operation_Node_Pointer->Array[i]!=NULL; i++)
            {
                if(NowTime-IO_Operation_Node_Pointer->Array[i]->LastTime>10 && IO_Operation_Node_Pointer->Array[i]->RandomMatchFlag==1)
                {
                    memset(&Packet,NULL,sizeof(SERVER_PACKET));
                    Packet.play=-2;
                    send(Client_Socket_Node_Pointer->SocketArray[i],(char *)&Packet,sizeof(SERVER_PACKET),0);
                }
                if(NowTime-IO_Operation_Node_Pointer->Array[i]->LastTime>120 && IO_Operation_Node_Pointer->Array[i]->PlayingChessFlag==1)
                {
                    memset(&Packet,NULL,sizeof(SERVER_PACKET));
                    Packet.play=-2;
                    send(IO_Operation_Node_Pointer->Array[i]->Opponent.Socket,(char *)&Packet,sizeof(SERVER_PACKET),0);
                }
            }
        }
        LeaveCriticalSection(&CS_ClientList);
        Sleep(1);
    }

    return 0;
}

DWORD WINAPI dispose_client_packet(LPVOID Parameter)
{
    int i;
    SERVER_PACKET SendPacket;
    DISPOSE_CLIENT_PACKET_PARAMETER *Dispose_Data=(DISPOSE_CLIENT_PACKET_PARAMETER *)Parameter;
    RANDOM_MATCH_NODE *Random_Match_Node_Pointer=NULL,*Random_Match_Parent_Pointer=NULL;

    //printf("GameMode:%d\t(Client:%s)\n",Dispose_Data->Packet.GameMode,inet_ntoa(Dispose_Data->IO_Data->ClientAddress.sin_addr));
    if(Dispose_Data->Packet.GameMode==1)
    {
        //好友对弈模式
        printf("接收到错误数据包。\n");
    }
    else if(Dispose_Data->Packet.GameMode==2)
    {
        //随机对弈模式
        if(Dispose_Data->IO_Data->RandomMatchFlag)
        {
            //已经在匹配队列中
            free(Dispose_Data);
            return -1;
        }
        if(Dispose_Data->IO_Data->Opponent.PlayGameFlag)
        {
            //需要结束当前游戏
            memset(&SendPacket,NULL,sizeof(SERVER_PACKET));
            SendPacket.play=-1;
            send(Dispose_Data->IO_Data->Opponent.Socket,(char *)&SendPacket,sizeof(SERVER_PACKET),0);
            Dispose_Data->IO_Data->Opponent.IO_Data->Opponent.Socket=INVALID_SOCKET;
            Dispose_Data->IO_Data->Opponent.IO_Data->Opponent.PlayGameFlag=0;
            Dispose_Data->IO_Data->Opponent.IO_Data=NULL;
            Dispose_Data->IO_Data->Opponent.PlayGameFlag=0;
        }
        EnterCriticalSection(&CS_RandomMatch);
        if(Random_Match_Header==NULL)
        {
            printf("加入到等待队列。\n");
            Random_Match_Header=(RANDOM_MATCH_NODE *)malloc(sizeof(RANDOM_MATCH_NODE));
            if(Random_Match_Header==NULL)
            {
                printf("malloc() error!\n");
                getch();
                exit(-1);
            }
            memset(Random_Match_Header,NULL,sizeof(RANDOM_MATCH_NODE));
            Random_Match_Header->GameType=Dispose_Data->Packet.GameType;
            Random_Match_Header->IO_Operation_Data_Pointer=Dispose_Data->IO_Data;
            Random_Match_Header->PlayerSocket=Dispose_Data->ClientSocket;
            Dispose_Data->IO_Data->RandomMatchFlag=1;
            Dispose_Data->IO_Data->LastTime=time(NULL);
        }
        else
        {
            for(Random_Match_Node_Pointer=Random_Match_Header,Random_Match_Parent_Pointer=NULL; Random_Match_Node_Pointer!=NULL; \
                    Random_Match_Node_Pointer=Random_Match_Node_Pointer->next)
            {
                if(Random_Match_Node_Pointer->GameType==Dispose_Data->Packet.GameType)
                    break;
                Random_Match_Parent_Pointer=Random_Match_Node_Pointer;
            }
            if(Random_Match_Node_Pointer!=NULL)
            {
                //匹配到对手
                printf("匹配成功。\n");
                Random_Match_Node_Pointer->IO_Operation_Data_Pointer->Opponent.Socket=Dispose_Data->ClientSocket;
                Random_Match_Node_Pointer->IO_Operation_Data_Pointer->Opponent.IO_Data=Dispose_Data->IO_Data;
                Dispose_Data->IO_Data->Opponent.Socket=Random_Match_Node_Pointer->PlayerSocket;
                Dispose_Data->IO_Data->Opponent.IO_Data=Random_Match_Node_Pointer->IO_Operation_Data_Pointer;
                Random_Match_Node_Pointer->IO_Operation_Data_Pointer->Opponent.PlayGameFlag=1;
                Dispose_Data->IO_Data->PlayGameFlag=1;
                Random_Match_Node_Pointer->IO_Operation_Data_Pointer->RandomMatchFlag=0;
                Dispose_Data->IO_Data->Opponent.PlayGameFlag=1;
                Dispose_Data->IO_Data->LastTime=Dispose_Data->IO_Data->Opponent.IO_Data->LastTime=time(NULL);

                if(Random_Match_Parent_Pointer==NULL)
                    Random_Match_Header=Random_Match_Node_Pointer->next;
                else
                    Random_Match_Parent_Pointer->next=Random_Match_Node_Pointer->next;
                free(Random_Match_Node_Pointer);

                memset(&SendPacket,NULL,sizeof(SERVER_PACKET));
                SendPacket.play=1;
                srand((unsigned)time(NULL)/rand());
                if(rand()%2==1)
                {
                    Dispose_Data->IO_Data->PlayingChessFlag=1;
                    send(Dispose_Data->ClientSocket,(char *)&SendPacket,sizeof(SERVER_PACKET),0);
                    SendPacket.play=0;
                    send(Dispose_Data->IO_Data->Opponent.Socket,(char *)&SendPacket,sizeof(SERVER_PACKET),0);
                }
                else
                {
                    Dispose_Data->IO_Data->Opponent.IO_Data->PlayingChessFlag=1;
                    send(Dispose_Data->IO_Data->Opponent.Socket,(char *)&SendPacket,sizeof(SERVER_PACKET),0);
                    SendPacket.play=0;
                    send(Dispose_Data->ClientSocket,(char *)&SendPacket,sizeof(SERVER_PACKET),0);
                }
            }
            else
            {
                //队列中没有对应的对手
                printf("加入到等待队列。\n");
                Random_Match_Parent_Pointer->next=(RANDOM_MATCH_NODE *)malloc(sizeof(RANDOM_MATCH_NODE));
                if(Random_Match_Parent_Pointer->next==NULL)
                {
                    printf("malloc() error!\n");
                    getch();
                    exit(-1);
                }
                memset(Random_Match_Parent_Pointer->next,NULL,sizeof(RANDOM_MATCH_NODE));
                Random_Match_Parent_Pointer->next->GameType=Dispose_Data->Packet.GameType;
                Random_Match_Parent_Pointer->next->PlayerSocket=Dispose_Data->ClientSocket;
                Random_Match_Parent_Pointer->next->IO_Operation_Data_Pointer=Dispose_Data->IO_Data;
                Dispose_Data->IO_Data->RandomMatchFlag=1;
                Dispose_Data->IO_Data->LastTime=time(NULL);
            }
        }
        LeaveCriticalSection(&CS_RandomMatch);
    }
    else if(Dispose_Data->Packet.GameMode==0)
    {
        //不改变当前游戏模式
        memset(&SendPacket,NULL,sizeof(SERVER_PACKET));
        Dispose_Data->IO_Data->LastTime=Dispose_Data->IO_Data->Opponent.IO_Data->LastTime=time(NULL);
        Dispose_Data->IO_Data->PlayingChessFlag=0;
        Dispose_Data->IO_Data->Opponent.IO_Data->PlayingChessFlag=1;
        SendPacket.play=1;
        for(i=0; i<4; i++)
            SendPacket.ChessCoord[i]=Dispose_Data->Packet.ChessCoord[i];

        if(Dispose_Data->IO_Data->Opponent.PlayGameFlag==0)
        {
            //对手已下线
            memset(&SendPacket,NULL,sizeof(SERVER_PACKET));
            SendPacket.play=-1;
            send(Dispose_Data->ClientSocket,(char *)&SendPacket,sizeof(SERVER_PACKET),0);
        }
        else if(send(Dispose_Data->IO_Data->Opponent.Socket,(char *)&SendPacket,sizeof(SERVER_PACKET),0)<=0)
        {
            //对手已下线
            memset(&SendPacket,NULL,sizeof(SERVER_PACKET));
            SendPacket.play=-1;
            send(Dispose_Data->ClientSocket,(char *)&SendPacket,sizeof(SERVER_PACKET),0);
        }
    }
    else
    {
        printf("接收到错误数据包。\n");
    }

    free(Dispose_Data);

    return 0;
}







// TwoAI.cpp : 定义 DLL 应用程序的导出函数。
//

#include "com_example_jni_ChessJNI.h"
#include "tree.h"
#include "queue.h"
#include <jni.h>
//#include <conio.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//#include <conio.h>
#include <time.h>

TREE_NODE *tree_root = NULL;

int judge_victory_four(int chessboard[][4]);

//queue
int add_queue_node(QUEUE_INFO *queue, TREE_NODE *root) {
	if (queue->head == NULL) {
		queue->head = queue->end = (QUEUE_NODE *) malloc(sizeof(QUEUE_NODE));
		if (queue->head == NULL)
			return -1;
		memset((void *) queue->head, 0, sizeof(QUEUE_NODE));

		queue->head->root = root;
	} else {
		queue->end->next = (QUEUE_NODE *) malloc(sizeof(QUEUE_NODE));
		if (queue->end->next == NULL)
			return -1;
		memset((void *) queue->end->next, 0, sizeof(QUEUE_NODE));

		queue->end->next->root = root;
		queue->end = queue->end->next;
	}

	return 0;
}

int peek_queue_node(QUEUE_INFO *queue, TREE_NODE **root) {
	if (queue->head == NULL)
		return -1;

	*root = queue->head->root;

	return 0;
}

int fetch_queue_node(QUEUE_INFO *queue, TREE_NODE **root) {
	QUEUE_NODE *tmp = NULL;

	if (queue->head == NULL)
		return -1;
	*root = queue->head->root;
	tmp = queue->head;
	queue->head = queue->head->next;
	free(tmp);

	return 0;
}

int destroy_queue(QUEUE_INFO *queue) {
	QUEUE_NODE *tmp = NULL;

	while (1) {
		if (queue->head == NULL)
			break;
		tmp = queue->head;
		queue->head = queue->head->next;
		free(tmp);
	}

	return 0;
}

//tree
int add_tree_node(TREE_NODE *root, int depth, int chessboard[][4],
		int old_coord[2], int new_coord[2], int dis_coord[2], int player) {
	//为根节点添加子节点
	int i, j, k;

	for (k = 0; k < 26; k++) {
		if (root->child[k] == NULL) {
			root->child[k] = (TREE_NODE *) malloc(sizeof(TREE_NODE));
			if (root->child[k] == NULL)
				return -1;
			memset(root->child[k], NULL, sizeof(TREE_NODE));

			for (i = 0; i < 4; i++) {
				for (j = 0; j < 4; j++)
					root->child[k]->chessboard[i][j] = chessboard[i][j];
			}
			root->child[k]->depth = depth;
			root->child[k]->player = player;
			root->child[k]->parent = root;
			root->child[k]->dis_coord[0] = dis_coord[0];
			root->child[k]->dis_coord[1] = dis_coord[1];
			root->child[k]->new_coord[0] = new_coord[0];
			root->child[k]->new_coord[1] = new_coord[1];
			root->child[k]->old_coord[0] = old_coord[0];
			root->child[k]->old_coord[1] = old_coord[1];
			root->child[k]->weight = -1;
			root->child[k]->number = k;
			return 0;
		}
	}
	return 0;
}

int calculate_weight(TREE_NODE *root) {
	//计算此节点下所有孩子的权重
	int i, j;

	for (i = 0; i < 26; i++) {
		if (root->child[i] != NULL) {
			root->child[i]->weight = 30;
			for (j = 0; j < 26; j++) {
				if (root->child[i]->child[j] != NULL) {
					if (root->child[i]->child[j]->dis_coord[0] != -1) {
						//对方吃了己方一个棋子
						root->child[i]->weight -= 1;
					}
				}
			}
			if (root->child[i]->dis_coord[0] != -1) {
				//有把对方棋子吃掉的情况
				root->child[i]->weight += 29;
				for (j = 0; j < 26; j++) {
					if (root->child[i]->child[j] != NULL) {
						if (root->child[i]->new_coord[0]
								== root->child[i]->child[j]->dis_coord[0]
								&& root->child[i]->new_coord[1]
										== root->child[i]->child[j]->dis_coord[1]) {
							//将对方棋子吃了后自己也被吃
							root->child[i]->weight -= 14;
							break;
						}
					}
				}
			} else {
				for (j = 0; j < 26; j++) {
					if (root->child[i]->child[j] != NULL) {
						if (root->child[i]->child[j]->dis_coord[0]
								== root->child[i]->new_coord[0]
								&& root->child[i]->child[j]->dis_coord[1]
										== root->child[i]->new_coord[1]) {
							//此步棋会被对方吃掉
							root->child[i]->weight = 1;
							break;
						}
					}
				}
			}
		}
	}

	return 0;
}

int pre_traversal_calculate_weight(TREE_NODE *root) {
	//先序遍历
	int i;

	if (root == NULL)
		return -1;
	for (i = 0; i < 26; i++) {
		if (root->child[i] != NULL) {
			if (root->child[i]->weight == -1)
				calculate_weight(root);    //计算权重
			else
				pre_traversal_calculate_weight(root->child[i]);
		}
	}
	return 0;
}

int pruning(TREE_NODE *root, int depth) {
	//剪枝算法  root:树根指针   depth:需要剪枝的深度
	QUEUE_INFO QueueInfo, QueueInfo2;
	TREE_NODE *tmp_root = NULL, *tmp2_root = NULL;
	int i, max_weigth = 0, first_weigth = 1;

	memset(&QueueInfo, NULL, sizeof(QUEUE_INFO));
	memset(&QueueInfo2, NULL, sizeof(QUEUE_INFO));

	//广度优先遍历
	add_queue_node(&QueueInfo, root);
	while (fetch_queue_node(&QueueInfo, &tmp_root) == 0) {
		if (tmp_root->depth == depth - 1) {
			for (i = 0; i < 26; i++) {
				if (tmp_root->child[i] != NULL) {
					add_queue_node(&QueueInfo2, tmp_root->child[i]);
					if (first_weigth) {
						max_weigth = tmp_root->child[i]->weight;
						first_weigth = 0;
					}
					if (max_weigth < tmp_root->child[i]->weight)
						max_weigth = tmp_root->child[i]->weight;
				}
			}
			//剪枝
			while (fetch_queue_node(&QueueInfo2, &tmp2_root) == 0) {
				if (tmp2_root->weight < max_weigth) {
					tmp2_root->parent->child[tmp2_root->number] = NULL;
					destroy_tree(&tmp2_root);
				}
			}
			max_weigth = 0;
			first_weigth = 1;
		}
		if (tmp_root->depth < depth) {
			for (i = 0; i < 26; i++) {
				if (tmp_root->child[i] != NULL)
					add_queue_node(&QueueInfo, tmp_root->child[i]);
			}
		}
	}

	return 0;
}

int measure_depth(TREE_NODE *root, int *max_depth) {
	//返回树的深度  max_depth初始值应为0
	int i;

	if (root == NULL)
		return -1;

	if (root->depth > *max_depth)
		*max_depth = root->depth;
	for (i = 0; i < 26; i++) {
		if (root->child[i] != NULL)
			measure_depth(root->child[i], max_depth);
	}

	return 0;
}

int get_tree_node_amount(TREE_NODE *root, unsigned long int *count) {
	//统计树中节点个数,count初始值应为0
	int i;

	if (root == NULL)
		return -1;
	(*count)++;
	for (i = 0; i < 26; i++) {
		if (root->child[i] != NULL)
			get_tree_node_amount(root->child[i], count);
	}

	return 0;
}

int get_win_depth(TREE_NODE *root, int player, int *winner_depth) {
	//获取胜者的最小深度.  winner_depth传入初始值应为-1
	int i;

	if (root == NULL)
		return -1;
	if (judge_victory_four(root->chessboard) == player) {
		if (*winner_depth == -1 || *winner_depth > root->depth) {
			//MessageBox(NULL, "Maybe win!", "Info", 0);
			*winner_depth = root->depth;
			return 0;
		}
	}
	for (i = 0; i < 26; i++) {
		if (root->child[i] != NULL)
			get_win_depth(root->child[i], player, winner_depth);
	}
	return 0;
}

int destroy_tree(TREE_NODE **root) {
	int i;

	//深度优先
	for (i = 0; i < 26; i++) {
		if ((*root)->child[i] != NULL)
			destroy_tree(&(*root)->child[i]);
	}
	free(*root);
	*root = NULL;

	return 0;
}

//main
int check_blocking(int chessboard[][3], int i, int j, int *x, int *y) {
	//x,y返回开口坐标
	if (i == 1 && j == 1) {
		if (chessboard[0][0] == 0) {
			if (x != NULL && y != NULL) {
				*x = 0;
				*y = 0;
			}
		} else if (chessboard[0][2] == 0) {
			if (x != NULL && y != NULL) {
				*x = 2;
				*y = 0;
			}
		} else if (chessboard[2][0] == 0) {
			if (x != NULL && y != NULL) {
				*x = 0;
				*y = 2;
			}
		} else {
			if (x != NULL && y != NULL) {
				*x = 2;
				*y = 2;
			}
		}
		return 0;
	}
	if (i == 0) {
		if (j == 0) {
			if (chessboard[i][j + 2] != 0) {
				if (chessboard[i + 1][j + 1] != 0) {
					if (chessboard[i + 2][j] != 0) {
						return 1;      //当前坐标处的棋子被围堵
					} else if (x != NULL && y != NULL) {
						*x = j;
						*y = i + 2;
					}
				} else if (x != NULL && y != NULL) {
					*x = j + 1;
					*y = i + 1;
				}
			} else if (x != NULL && y != NULL) {
				*x = j + 2;
				*y = i;
			}
		} else if (j == 2) {
			if (chessboard[i][j - 2] != 0) {
				if (chessboard[i + 1][j - 1] != 0) {
					return 1;
				} else if (x != NULL && y != NULL) {
					*x = j - 1;
					*y = i + 1;
				}
			} else if (x != NULL && y != NULL) {
				*x = j - 2;
				*y = i;
			}
		} else
			return -1;
	} else if (i == 2) {
		if (j == 0) {
			if (chessboard[i - 2][j] != 0) {
				if (chessboard[i - 1][j + 1] != 0) {
					if (chessboard[i][j + 2] != 0) {
						return 1;
					} else if (x != NULL && y != NULL) {
						*x = j + 2;
						*y = i;
					}
				} else if (x != NULL && y != NULL) {
					*x = j + 1;
					*y = i - 1;
				}
			} else if (x != NULL && y != NULL) {
				*x = j;
				*y = i - 2;
			}
		} else if (j == 2) {
			if (chessboard[i][j - 2] != 0) {
				if (chessboard[i - 1][j - 1] != 0) {
					return 1;
				} else if (x != NULL && y != NULL) {
					*x = j - 1;
					*y = i - 1;
				}
			} else if (x != NULL && y != NULL) {
				*x = j - 2;
				*y = i;
			}
		} else
			return -1;
	} else
		return -1;

	return 0;
}

int judge_victory(int chessboard[][3]) {
	//返回0:未分出胜负 1：白子胜 2：黑子胜
	int i, j, bai = 0, hei = 0;

	for (i = 0; i < 3; i++) {
		for (j = 0; j < 3; j++) {
			if (chessboard[i][j] != 0 && chessboard[i][j] != -1) {
				//此处有棋子
				if (check_blocking(chessboard, i, j, NULL, NULL) == 1) {
					if (chessboard[i][j] == 1)
						hei++;
					else
						bai++;
				}
			}
			if (bai == 2)
				return 1;       //白棋胜
			if (hei == 2)
				return 2;		  //黑棋胜
		}
	}

	return 0;
}

int *AI(int chessboard[][3], int player, int top_flag) {
	static int first_step = 1;
	int i, j, k, x, y, opponent, ret_1, ret_2;
	int tempchess[3][3] = { 0 }, chesscoord[2][4] = { 0 };
	int *coord = NULL;

	coord = (int *) malloc(5 * sizeof(int));
	if (coord == NULL || chessboard == NULL)
		return NULL;
	for (i = 0; i < 5; i++)
		coord[i] = -1;

	if (first_step) {
		first_step = 0;
		if (chessboard[1][1] == 0) {
			if (player == 1) {
				chessboard[0][0] = 0;
				chessboard[1][1] = player;
				coord[0] = 0;
				coord[1] = 0;
				coord[2] = 0;
				coord[3] = 1;
				coord[4] = 1;
				return coord;
			} else {
				chessboard[2][0] = 0;
				chessboard[1][1] = player;
				coord[0] = 0;
				coord[1] = 2;
				coord[2] = 0;
				coord[3] = 1;
				coord[4] = 1;
				return coord;
			}
		}
	}

	if (player == 1)
		opponent = 2;
	else
		opponent = 1;

	for (i = 0; i < 2; i++) {
		for (j = 0; j < 4; j++)
			chesscoord[i][j] = -1;
	}

	//获取己方两处棋子的围堵情况
	for (i = k = 0; i < 3; i++) {
		for (j = 0; j < 3; j++) {
			if (chessboard[i][j] == player) {
				if (check_blocking(chessboard, i, j, &x, &y) == 0) {
					//此处棋子没被围堵
					chesscoord[k][0] = i;
					chesscoord[k][1] = j;
					chesscoord[k][2] = y;
					chesscoord[k][3] = x;
					k++;
				}
			}
		}
	}

	//检测下一步是否能胜
	for (i = 0; i < 3; i++) {
		for (j = 0; j < 3; j++)
			tempchess[i][j] = chessboard[i][j];
	}
	if (chesscoord[0][0] != -1) {
		tempchess[chesscoord[0][0]][chesscoord[0][1]] = 0;
		tempchess[chesscoord[0][2]][chesscoord[0][3]] = player;
		if (judge_victory(tempchess) == player) {
			chessboard[chesscoord[0][0]][chesscoord[0][1]] = 0;
			chessboard[chesscoord[0][2]][chesscoord[0][3]] = player;
			coord[0] = player;
			for (i = 0; i < 4; i++)
				coord[i + 1] = chesscoord[0][i];
			return coord;
		}
	} else
		return NULL;
	for (i = 0; i < 3; i++) {
		for (j = 0; j < 3; j++)
			tempchess[i][j] = chessboard[i][j];
	}
	if (chesscoord[1][0] != -1) {
		tempchess[chesscoord[1][0]][chesscoord[1][1]] = 0;
		tempchess[chesscoord[1][2]][chesscoord[1][3]] = player;
		if (judge_victory(tempchess) == player) {
			chessboard[chesscoord[1][0]][chesscoord[1][1]] = 0;
			chessboard[chesscoord[1][2]][chesscoord[1][3]] = player;
			coord[0] = player;
			for (i = 0; i < 4; i++)
				coord[i + 1] = chesscoord[1][i];
			return coord;
		}
	} else {
		chessboard[chesscoord[0][0]][chesscoord[0][1]] = 0;
		chessboard[chesscoord[0][2]][chesscoord[0][3]] = player;
		coord[0] = 0;
		for (i = 0; i < 4; i++)
			coord[i + 1] = chesscoord[0][i];
		return coord;
	}

	if (top_flag == 0)
		return coord;
	for (i = 0; i < 3; i++) {
		for (j = 0; j < 3; j++)
			tempchess[i][j] = chessboard[i][j];
	}
	tempchess[chesscoord[0][0]][chesscoord[0][1]] = 0;
	tempchess[chesscoord[0][2]][chesscoord[0][3]] = player;
	//递归推导对方走法
	if (AI(tempchess, opponent, 0)[0] == opponent) {
		//此步不能走
		//MessageBox(NULL, "1-->0", "pause", MB_OK);
		ret_1 = 0;
	} else
		ret_1 = 1;
	for (i = 0; i < 3; i++) {
		for (j = 0; j < 3; j++)
			tempchess[i][j] = chessboard[i][j];
	}
	tempchess[chesscoord[1][0]][chesscoord[1][1]] = 0;
	tempchess[chesscoord[1][2]][chesscoord[1][3]] = player;
	if (AI(tempchess, opponent, 0)[0] == opponent) {
		//此步不能走
		//MessageBox(NULL, "2-->0", "pause", MB_OK);
		ret_2 = 0;
	} else
		ret_2 = 1;

	if (ret_1 == 1 && ret_2 == 1) {
		//MessageBox(NULL,"pause","pause",MB_OK);
		srand (time(NULL));i = rand() % 2;
		if (i == 1)
		{
			chessboard[chesscoord[0][0]][chesscoord[0][1]] = 0;
			chessboard[chesscoord[0][2]][chesscoord[0][3]] = player;
			coord[0] = 0;
			for (i = 0; i < 4; i++)
			coord[i + 1] = chesscoord[0][i];
			return coord;
		}
		else
		{
			chessboard[chesscoord[1][0]][chesscoord[1][1]] = 0;
			chessboard[chesscoord[1][2]][chesscoord[1][3]] = player;
			coord[0] = 0;
			for (i = 0; i < 4; i++)
			coord[i + 1] = chesscoord[1][i];
			return coord;
		}
	}
	else if (ret_1 == 1)
	{
		chessboard[chesscoord[0][0]][chesscoord[0][1]] = 0;
		chessboard[chesscoord[0][2]][chesscoord[0][3]] = player;
		coord[0] = 0;
		for (i = 0; i < 4; i++)
		coord[i + 1] = chesscoord[0][i];
		return coord;
	}
	else
	{
		chessboard[chesscoord[1][0]][chesscoord[1][1]] = 0;
		chessboard[chesscoord[1][2]][chesscoord[1][3]] = player;
		coord[0] = 0;
		for (i = 0; i < 4; i++)
		coord[i + 1] = chesscoord[1][i];
		return coord;
	}

	return coord;
}

JNIEXPORT jint JNICALL Java_com_example_jni_ChessJNI_tjudge_1tvictory(
		JNIEnv *env, jobject obj, jobjectArray array) {
	int chessboard[3][3];
	jarray myarray;
	int size = env->GetArrayLength(array);
	for (int i = 0; i < size && i < 3; i++) {
		myarray = (jarray) env->GetObjectArrayElement(array, i);

		int col = env->GetArrayLength(myarray);
		jint *coldata = env->GetIntArrayElements((jintArray) myarray, 0);
		for (int j = 0; j < col && j < 3; j++) {
			chessboard[i][j] = coldata[j];
		}
		env->ReleaseIntArrayElements((jintArray) myarray, coldata, 0);
	}

	return judge_victory(chessboard);
}

JNIEXPORT jintArray JNICALL Java_com_example_jni_ChessJNI_tAI(JNIEnv * env,
		jobject obj, jobjectArray array, jint m, jint n) {
	int *p;
	int a[5];
	int chessboard[3][3];
	jarray myarray;
	int size = env->GetArrayLength(array);
	jintArray iarr = env->NewIntArray(5);
	for (int i = 0; i < size && i < 3; i++) {
		myarray = (jarray) env->GetObjectArrayElement(array, i);

		int col = env->GetArrayLength(myarray);
		jint *coldata = env->GetIntArrayElements((jintArray) myarray, 0);
		for (int j = 0; j < col && j < 3; j++) {
			chessboard[i][j] = coldata[j];
		}
		env->ReleaseIntArrayElements((jintArray) myarray, coldata, 0);
	}
	p = AI(chessboard, m, n);
	if (p != NULL) {
		for (int q = 0; q < 5; q++) {
			a[q] = *(p + q);
		}
		free(p);
	}
	env->SetIntArrayRegion(iarr, 0, 5, a);

	return iarr;
}

//FOUR
int judge_victory_four(int chessboard[][4]) {
	//返回0:未分出胜负 1：白子胜 2：黑子胜
	int i, j, bai = 0, hei = 0;

	for (i = 0; i < 4; i++) {
		for (j = 0; j < 4; j++) {
			if (chessboard[i][j] == 1)
				bai++;
			else if (chessboard[i][j] == 2)
				hei++;
		}
	}
	if (bai <= 1)
		return 2;     //黑棋胜
	else if (hei <= 1)
		return 1;    //白棋胜

	return 0;
}

int check_blocking(int chessboard[][4], int i, int j, int open_coord[4][2]) {
	int m, n;
	int dir[4] = { 0 };

	for (m = 0; m < 4; m++) {
		for (n = 0; n < 2; n++)
			open_coord[m][n] = -1;
	}

	if (i != 0)
		dir[3] = 1;		//向上不越界
	if (j != 0)
		dir[2] = 1;		//向左不越界
	if (i != 3)
		dir[1] = 1;		//向下不越界
	if (j != 3)
		dir[0] = 1;		//向右不越界

	if (dir[0] == 1) {
		if (chessboard[i][j + 1] == 0) {
			open_coord[0][0] = i;
			open_coord[0][1] = j + 1;
		}
	}
	if (dir[1] == 1) {
		if (chessboard[i + 1][j] == 0) {
			open_coord[1][0] = i + 1;
			open_coord[1][1] = j;
		}
	}
	if (dir[2] == 1) {
		if (chessboard[i][j - 1] == 0) {
			open_coord[2][0] = i;
			open_coord[2][1] = j - 1;
		}
	}
	if (dir[3] == 1) {
		if (chessboard[i - 1][j] == 0) {
			open_coord[3][0] = i - 1;
			open_coord[3][1] = j;
		}
	}

	for (i = 0; i < 4; i++) {
		if (open_coord[i][0] == -1 || open_coord[i][1] == -1)
			open_coord[i][0] = open_coord[i][1] = -1;
	}

	return 0;
}

int *eat_chess(int chessboard[][4], int i, int j) {
	int *dis_coord = NULL;
	int player, opponent, m, n, player_count, opponent_count, link_flag;

	player = chessboard[i][j];
	if (player == 1)
		opponent = 2;
	else
		opponent = 1;

	dis_coord = (int *) malloc(2 * sizeof(int));
	if (dis_coord == NULL)
		return NULL;
	dis_coord[0] = dis_coord[1] = -1;

	//检查横坐标
	for (m = 1, player_count = opponent_count = link_flag = 0; m < 4; m++) {
		if (j + m < 4) {
			//横坐标正向未越界
			if (chessboard[i][j + m] == player)
				player_count++;
			else if (chessboard[i][j + m] == opponent)
				opponent_count++;
		}
		if (j - m >= 0) {
			//横坐标反向未越界
			if (chessboard[i][j - m] == player)
				player_count++;
			else if (chessboard[i][j - m] == opponent)
				opponent_count++;
		}
		if (m == 1) {
			if (player_count == 1)
				link_flag = 2;
			if (link_flag == 2 && opponent_count == 1)
				link_flag = 3;
		} else if (m == 2 && link_flag == 2) {
			if (player_count == 1 && opponent_count == 1)
				link_flag = 3;
		}
	}
	if (link_flag == 3) {
		for (n = m = 0; m < 4; m++) {
			if (chessboard[i][m] != 0)
				n++;
			else
				n = 0;
			if (n == 3) {
				link_flag = 4;
				break;
			}
		}
	}
	if (link_flag == 4 && player_count == 1 && opponent_count == 1) {
		for (m = 0; m < 4; m++) {
			if (chessboard[i][m] == opponent) {
				chessboard[i][m] = 0;
				dis_coord[0] = i;
				dis_coord[1] = m;
				return dis_coord;
			}
		}
	}

	//检查纵坐标
	for (m = 1, player_count = opponent_count = link_flag = 0; m < 4; m++) {
		if (i + m < 4) {
			//纵坐标正向未越界
			if (chessboard[i + m][j] == player)
				player_count++;
			else if (chessboard[i + m][j] == opponent)
				opponent_count++;
		}
		if (i - m >= 0) {
			//纵坐标反向未越界
			if (chessboard[i - m][j] == player)
				player_count++;
			else if (chessboard[i - m][j] == opponent)
				opponent_count++;
		}
		if (m == 1) {
			if (player_count == 1)
				link_flag = 2;
			if (link_flag == 2 && opponent_count == 1)
				link_flag = 3;
		} else if (m == 2 && link_flag == 2) {
			if (player_count == 1 && opponent_count == 1)
				link_flag = 3;
		}
	}
	if (link_flag == 3) {
		for (n = m = 0; m < 4; m++) {
			if (chessboard[m][j] != 0)
				n++;
			else
				n = 0;
			if (n == 3) {
				link_flag = 4;
				break;
			}
		}
	}
	if (link_flag == 4 && player_count == 1 && opponent_count == 1) {
		for (m = 0; m < 4; m++) {
			if (chessboard[m][j] == opponent) {
				chessboard[m][j] = 0;
				dis_coord[0] = m;
				dis_coord[1] = j;
				return dis_coord;
			}
		}
	}

	return dis_coord;
}

int new_node_child(TREE_NODE *root, int player, int depth) {
	int i, j, m, n, k, testchess[4][4], open_coord[4][2], old_coord[2],
			new_coord[2];
	int *dis_coord = NULL;

	for (m = 0; m < 4; m++) {
		for (n = 0; n < 4; n++) {
			//遍历整个棋盘
			if (root->chessboard[m][n] == player) {
				for (i = 0; i < 4; i++) {
					for (j = 0; j < 2; j++)
						open_coord[i][j] = -1;
				}
				check_blocking(root->chessboard, m, n, open_coord); //获取当前棋子的开路情况
				for (k = 0; k < 4; k++) {
					if (open_coord[k][0] == -1)
						continue;
					for (i = 0; i < 4; i++) {
						for (j = 0; j < 4; j++)
							testchess[i][j] = root->chessboard[i][j];
					}
					testchess[m][n] = 0;
					testchess[open_coord[k][0]][open_coord[k][1]] = player;
					dis_coord = eat_chess(testchess, open_coord[k][0],
							open_coord[k][1]);
					if (dis_coord == NULL)
						return -1;
					old_coord[0] = m;
					old_coord[1] = n;
					new_coord[0] = open_coord[k][0];
					new_coord[1] = open_coord[k][1];
					add_tree_node(root, depth, testchess, old_coord, new_coord,
							dis_coord, player);
					free(dis_coord);
				}
			}
		}
	}
	return 0;
}

int *Four_AI(int chessboard[][4], int player) {
	int i, j, n, depth, pruning_depth, limit, opponent, max_depth,
			min_win_depth, min_lose_depth, _player = player;
	int *coord = NULL;
	unsigned long int tree_node_count;
	TREE_NODE *root = NULL;
	QUEUE_INFO QueueInfo, QueueInfo2;

	memset(&QueueInfo, NULL, sizeof(QUEUE_INFO));

	coord = (int *) malloc(6 * sizeof(int));
	if (coord == NULL || chessboard == NULL) {
		//MessageBox(NULL, "malloc error", "info", 0);
		return NULL;
	}
	for (i = 0; i < 6; i++)
		coord[i] = -1;

	if (player == 1)
		opponent = 2;
	else
		opponent = 1;

	//创建树根
	tree_root = (TREE_NODE *) malloc(sizeof(TREE_NODE));
	if (tree_root == NULL) {
		//MessageBox(NULL,"malloc error","info",0);
		return NULL;
	}
	memset(tree_root, NULL, sizeof(TREE_NODE));
	for (i = 0; i < 4; i++) {
		for (j = 0; j < 4; j++)
			tree_root->chessboard[i][j] = chessboard[i][j];
	}
	tree_root->player = opponent;
	tree_root->depth = 1;
	tree_root->dis_coord[0] = tree_root->dis_coord[1] = -1;
	tree_root->new_coord[0] = tree_root->new_coord[1] = -1;
	tree_root->old_coord[0] = tree_root->old_coord[1] = -1;
	tree_root->weight = -1;

	limit = 4;
	pruning_depth = 2;
	depth = 2;
	add_queue_node(&QueueInfo, tree_root);
	while (1) {
		if (fetch_queue_node(&QueueInfo, &root) != 0)
			break;
		if (depth != root->depth + 1) {
			if (_player == 1)
				_player = 2;
			else
				_player = 1;
			depth = root->depth + 1;
		}
		if (depth != limit) {
			if (judge_victory_four(chessboard))
				continue;
			if (new_node_child(root, _player, depth) != 0) {
				//MessageBox(NULL,"new_node_child() error","Info",0);
				return NULL;
			}
		} else {
			destroy_queue(&QueueInfo);
			pre_traversal_calculate_weight(tree_root);
			pruning(tree_root, pruning_depth);
			pruning_depth++;
			limit += 2;

			memset(&QueueInfo2, NULL, sizeof(QUEUE_INFO));
			add_queue_node(&QueueInfo2, tree_root);
			while (1) {
				if (fetch_queue_node(&QueueInfo2, &root) != 0)
					break;
				if (root->depth == pruning_depth) {
					add_queue_node(&QueueInfo, root);
				}
				if (root->depth < pruning_depth) {
					for (i = 0; i < 26; i++) {
						if (root->child[i] != NULL)
							add_queue_node(&QueueInfo2, root->child[i]);
					}
				}
			}
			tree_node_count = 0;
			get_tree_node_amount(tree_root, &tree_node_count);
			if (tree_node_count > 500) {
				destroy_queue(&QueueInfo);
				destroy_queue(&QueueInfo2);
				break;
			}
			continue;
		}
		for (j = 0; j < 26; j++) {
			if (root->child[j] != NULL) {
				add_queue_node(&QueueInfo, root->child[j]);
			}
		}
	}

	destroy_queue(&QueueInfo);

	for (i = 0; i < 26; i++) {
		max_depth = 0;
		min_win_depth = min_lose_depth = -1;
		if (tree_root->child[i] != NULL) {
			measure_depth(tree_root->child[i], &max_depth);
			max_depth++;
			get_win_depth(tree_root->child[i], player, &min_win_depth);
			get_win_depth(tree_root->child[i], opponent, &min_lose_depth);
			tree_root->child[i]->weight = tree_root->child[i]->weight
					+ (max_depth - min_win_depth)
					- (max_depth - min_lose_depth);
		}
	}
	pruning(tree_root, 2);

	for (i = n = 0; i < 26; i++) {
		if (tree_root->child[i] != NULL)
			n++;
	}
	if (n == 0) {
		//MessageBox(NULL,"divisor is zero","error",0);
		return NULL;
	}
	srand(time(NULL) * n);
	i = rand();
	j = i % n;
	for (n = 0; n < 26; n++) {
		if (tree_root->child[n] != NULL) {
			if (j == 0) {
				coord[0] = tree_root->child[n]->old_coord[0];
				coord[1] = tree_root->child[n]->old_coord[1];
				coord[2] = tree_root->child[n]->new_coord[0];
				coord[3] = tree_root->child[n]->new_coord[1];
				coord[4] = tree_root->child[n]->dis_coord[0];
				coord[5] = tree_root->child[n]->dis_coord[1];
				chessboard[coord[0]][coord[1]] = 0;
				chessboard[coord[2]][coord[3]] = player;
				if (coord[5] != -1) {
					chessboard[coord[4]][coord[5]] = 0;
				}
				destroy_tree(&tree_root);
				return coord;
			} else
				j--;
		}
	}
	destroy_tree(&tree_root);

	return NULL;
}

JNIEXPORT jintArray JNICALL Java_com_example_jni_ChessJNI_feat_1fchess(
		JNIEnv * env, jobject obj, jobjectArray array, jint m, jint n) {
	int *p;
	int a[5];
	int chessboard[4][4];
	jarray myarray;
	int size = env->GetArrayLength(array);
	jintArray iarr = env->NewIntArray(2);
	for (int i = 0; i < size && i < 4; i++) {
		myarray = (jarray) env->GetObjectArrayElement(array, i);
		int col = env->GetArrayLength(myarray);
		jint *coldata = env->GetIntArrayElements((jintArray) myarray, 0);
		for (int j = 0; j < col && j < 4; j++) {
			chessboard[i][j] = coldata[j];
		}
		env->ReleaseIntArrayElements((jintArray) myarray, coldata, 0);
	}
	p = eat_chess(chessboard, m, n);
	if (p != NULL) {
		for (int q = 0; q < 2; q++) {
			a[q] = *(p + q);
		}
		free(p);
	}
	env->SetIntArrayRegion(iarr, 0, 2, a);
	return iarr;

}

JNIEXPORT jint JNICALL Java_com_example_jni_ChessJNI_fjudge_1fvictory_1ffour(
		JNIEnv * env, jobject obj, jobjectArray array) {
	int chessboard[4][4];
	jarray myarray;
	int size = env->GetArrayLength(array);
	for (int i = 0; i < size && i < 4; i++) {
		myarray = (jarray) env->GetObjectArrayElement(array, i);

		int col = env->GetArrayLength(myarray);
		jint *coldata = env->GetIntArrayElements((jintArray) myarray, 0);
		for (int j = 0; j < col && j < 4; j++) {
			chessboard[i][j] = coldata[j];
		}
		env->ReleaseIntArrayElements((jintArray) myarray, coldata, 0);
	}

	return judge_victory_four(chessboard);

}

JNIEXPORT jintArray JNICALL Java_com_example_jni_ChessJNI_fFour_1fAI(
		JNIEnv * env, jobject obj, jobjectArray array, jint m) {
	int *p;
	int a[6];
	int chessboard[4][4];
	jarray myarray;
	int size = env->GetArrayLength(array);
	jintArray iarr = env->NewIntArray(6);
	for (int i = 0; i < size && i < 4; i++) {
		myarray = (jarray) env->GetObjectArrayElement(array, i);

		int col = env->GetArrayLength(myarray);
		jint *coldata = env->GetIntArrayElements((jintArray) myarray, 0);
		for (int j = 0; j < col && j < 4; j++) {
			chessboard[i][j] = coldata[j];
		}
		env->ReleaseIntArrayElements((jintArray) myarray, coldata, 0);
	}
	p = Four_AI(chessboard, m);
	if (p != NULL) {
		for (int q = 0; q < 6; q++) {
			a[q] = *(p + q);
		}
		free(p);
	}
	env->SetIntArrayRegion(iarr, 0, 6, a);
	return iarr;
}

int njudge_victroy_nine(int chessboard[5][7]) {
	int i, j, hei, bai, player;

	if (chessboard[2][6] == 1)
		player = 1;
	else if (chessboard[2][6] == 2)
		player = 2;
	else
		player = 0;

	for (i = hei = bai = 0; i < 5; i++) {
		for (j = 0; j < 7; j++) {
			if (chessboard[i][j] == 1)
				bai++;
			else if (chessboard[i][j] == 2)
				hei++;
		}
	}

	if (player == 1 && bai == 1)
		return 2;      //黑棋胜
	if (player == 2 && hei == 1)
		return 1;      //白棋胜

	if (bai == 0)
		return 1;
	if (hei == 0)
		return 2;

	return 0;
}

int *alter_chess(int chessboard[5][7], int i, int j) {
	int player, opponent, m, n, start_index = 0, end_index = 0, check_diagonal =
			0, player_count, opponent_count, alter_flag;
	int *alter_coord = NULL, *ret_coord = NULL;

	player = chessboard[i][j];
	if (player == 1)
		opponent = 2;
	else
		opponent = 1;

	//检查横坐标
	for (m = 1, ret_coord = NULL, player_count = opponent_count = alter_flag = 0;
			m < 7; m++) {
		if (i != 2) {
			if (j + m < 5) {
				//横坐标正向未越界
				if (chessboard[i][j + m] == player)
					player_count++;
				else if (chessboard[i][j + m] == opponent)
					opponent_count++;
			}
		} else if (i == 2) {
			if (j + m < 7) {
				//横坐标正向未越界
				if (chessboard[i][j + m] == player)
					player_count++;
				else if (chessboard[i][j + m] == opponent)
					opponent_count++;
			}
		}
		if (j - m >= 0) {
			//横坐标反向未越界
			if (chessboard[i][j - m] == player)
				player_count++;
			else if (chessboard[i][j - m] == opponent)
				opponent_count++;
		}
		if (m == 1) {
			if (player_count > 0 || opponent_count == 0)
				break;
			alter_flag = opponent_count;
		} else if (m == 2) {
			if (player_count == 1 && opponent_count == 1)
				alter_flag = 2;
			if (alter_flag != 2) {
				alter_flag = 0;
				break;
			}
		} else if (m > 2) {
			if (player_count > 1 || opponent_count > 2
					|| opponent_count + player_count > 2) {
				alter_flag = 0;
				break;
			}
		}
	}
	if (alter_flag == 2) {
		for (m = 0; m < 7; m++) {
			if (i != 2 && m > 4)
				break;
			if (chessboard[i][m] == opponent) {
				if (alter_coord == NULL) {
					alter_coord = (int *) malloc(2 * sizeof(int));
					if (alter_coord == NULL)
						return NULL;
					alter_coord[end_index] = i;
					alter_coord[end_index + 1] = m;
					end_index += 2;
				} else {
					alter_coord = (int *) realloc(alter_coord,
							(end_index + 2) * sizeof(int));
					if (alter_coord == NULL)
						return NULL;
					alter_coord[end_index] = i;
					alter_coord[end_index + 1] = m;
					end_index += 2;
				}
			}
		}
		alter_coord = (int *) realloc(alter_coord,
				(end_index + 2) * sizeof(int));
		if (alter_coord == NULL)
			return NULL;
		alter_coord[end_index] = alter_coord[end_index + 1] = -1;
		for (m = 0; m < end_index; m += 2) {
			chessboard[alter_coord[m]][alter_coord[m + 1]] = player;
			ret_coord = alter_chess(chessboard, alter_coord[m],
					alter_coord[m + 1]);
			if (ret_coord != NULL) {
				for (n = 0; ret_coord[n] != -1; n += 2) {
					alter_coord = (int *) realloc(alter_coord,
							(end_index + 2) * sizeof(int));
					if (alter_coord == NULL)
						return NULL;
					alter_coord[end_index] = ret_coord[n];
					alter_coord[end_index + 1] = ret_coord[n + 1];
					end_index += 2;
				}
				free(ret_coord);
				ret_coord = NULL;
				alter_coord = (int *) realloc(alter_coord,
						(end_index + 2) * sizeof(int));
				if (alter_coord == NULL)
					return NULL;
				alter_coord[end_index] = alter_coord[end_index + 1] = -1;
			}
		}
		start_index = end_index;
	}

	//检查纵坐标
	for (m = 1, player_count = opponent_count = alter_flag = 0; m < 5; m++) {
		if (i + m < 5) {
			//纵坐标正向未越界
			if (chessboard[i + m][j] == player)
				player_count++;
			else if (chessboard[i + m][j] == opponent)
				opponent_count++;
		}
		if (i - m >= 0) {
			//纵坐标反向未越界
			if (chessboard[i - m][j] == player)
				player_count++;
			else if (chessboard[i - m][j] == opponent)
				opponent_count++;
		}
		if (m == 1) {
			if (player_count > 0 || opponent_count == 0)
				break;
			alter_flag = opponent_count;
		} else if (m == 2) {
			if (player_count == 1 && opponent_count == 1)
				alter_flag = 2;
			if (alter_flag != 2) {
				alter_flag = 0;
				break;
			}
		} else if (m > 2) {
			if (player_count > 1 || opponent_count > 2
					|| opponent_count + player_count > 2) {
				alter_flag = 0;
				break;
			}
		}
	}
	if (alter_flag == 2) {
		for (m = 0; m < 5; m++) {
			if (chessboard[m][j] == opponent) {
				if (alter_coord == NULL) {
					alter_coord = (int *) malloc(2 * sizeof(int));
					if (alter_coord == NULL)
						return NULL;
					alter_coord[end_index] = m;
					alter_coord[end_index + 1] = j;
					end_index += 2;
				} else {
					alter_coord = (int *) realloc(alter_coord,
							(end_index + 2) * sizeof(int));
					if (alter_coord == NULL)
						return NULL;
					alter_coord[end_index] = m;
					alter_coord[end_index + 1] = j;
					end_index += 2;
				}
			}
		}
		alter_coord = (int *) realloc(alter_coord,
				(end_index + 2) * sizeof(int));
		if (alter_coord == NULL)
			return NULL;
		alter_coord[end_index] = alter_coord[end_index + 1] = -1;
		for (m = start_index; m < end_index; m += 2) {
			chessboard[alter_coord[m]][alter_coord[m + 1]] = player;
			ret_coord = alter_chess(chessboard, alter_coord[m],
					alter_coord[m + 1]);
			if (ret_coord != NULL) {
				for (n = 0; ret_coord[n] != -1; n += 2) {
					alter_coord = (int *) realloc(alter_coord,
							(end_index + 2) * sizeof(int));
					if (alter_coord == NULL)
						return NULL;
					alter_coord[end_index] = ret_coord[n];
					alter_coord[end_index + 1] = ret_coord[n + 1];
					end_index += 2;
				}
				free(ret_coord);
				ret_coord = NULL;
				alter_coord = (int *) realloc(alter_coord,
						(end_index + 2) * sizeof(int));
				if (alter_coord == NULL)
					return NULL;
				alter_coord[end_index] = alter_coord[end_index + 1] = -1;
			}
		}
		start_index = end_index;
	}

	for (m = check_diagonal = 0; m < 6; m++) {
		if ((i - m == 1 && j + m == 1) || (i + m == 1 && j - m == 1)) {
			check_diagonal = 1;
			break;
		} else if ((i - m == 2 && j + m == 2) || (i + m == 2 && j - m == 2)) {
			check_diagonal = 1;
			break;
		} else if ((i - m == 3 && j + m == 3) || (i + m == 3 && j - m == 3)) {
			check_diagonal = 1;
			break;
		}
	}

	if (check_diagonal) {
		//检查内斜直线坐标
		for (m = 1, player_count = opponent_count = alter_flag = 0; m < 6;
				m++) {
			if (i - m >= 0 && j + m < 6) {
				//内斜直线正向未越界
				if (chessboard[i - m][j + m] == player)
					player_count++;
				else if (chessboard[i - m][j + m] == opponent)
					opponent_count++;
			}
			if (i + m < 5 && j - m >= 0) {
				//内斜直线反向未越界
				if (chessboard[i + m][j - m] == player)
					player_count++;
				else if (chessboard[i + m][j - m] == opponent)
					opponent_count++;
			}
			if (m == 1) {
				if (player_count > 0 || opponent_count == 0)
					break;
				alter_flag = opponent_count;
			} else if (m == 2) {
				if (player_count == 1 && opponent_count == 1)
					alter_flag = 2;
				if (alter_flag != 2) {
					alter_flag = 0;
					break;
				}
			} else if (m > 2) {
				if (player_count > 1 || opponent_count > 2
						|| opponent_count + player_count > 2) {
					alter_flag = 0;
					break;
				}
			}
		}
		if (alter_flag == 2) {
			for (m = 0; m < 6; m++) {
				if (i - m >= 0 && j + m < 6) {
					if (chessboard[i - m][j + m] == opponent) {
						if (alter_coord == NULL) {
							alter_coord = (int *) malloc(2 * sizeof(int));
							if (alter_coord == NULL)
								return NULL;
							alter_coord[end_index] = i - m;
							alter_coord[end_index + 1] = j + m;
							end_index += 2;
						} else {
							alter_coord = (int *) realloc(alter_coord,
									(end_index + 2) * sizeof(int));
							if (alter_coord == NULL)
								return NULL;
							alter_coord[end_index] = i - m;
							alter_coord[end_index + 1] = j + m;
							end_index += 2;
						}
					}
				}
				if (i + m < 5 && j - m >= 0) {
					if (chessboard[i + m][j - m] == opponent) {
						if (alter_coord == NULL) {
							alter_coord = (int *) malloc(2 * sizeof(int));
							if (alter_coord == NULL)
								return NULL;
							alter_coord[end_index] = i + m;
							alter_coord[end_index + 1] = j - m;
							end_index += 2;
						} else {
							alter_coord = (int *) realloc(alter_coord,
									(end_index + 2) * sizeof(int));
							if (alter_coord == NULL)
								return NULL;
							alter_coord[end_index] = i + m;
							alter_coord[end_index + 1] = j - m;
							end_index += 2;
						}
					}
				}
			}
			alter_coord = (int *) realloc(alter_coord,
					(end_index + 2) * sizeof(int));
			if (alter_coord == NULL)
				return NULL;
			alter_coord[end_index] = alter_coord[end_index + 1] = -1;
			for (m = start_index; m < end_index; m += 2) {
				chessboard[alter_coord[m]][alter_coord[m + 1]] = player;
				ret_coord = alter_chess(chessboard, alter_coord[m],
						alter_coord[m + 1]);
				if (ret_coord != NULL) {
					for (n = 0; ret_coord[n] != -1; n += 2) {
						alter_coord = (int *) realloc(alter_coord,
								(end_index + 2) * sizeof(int));
						if (alter_coord == NULL)
							return NULL;
						alter_coord[end_index] = ret_coord[n];
						alter_coord[end_index + 1] = ret_coord[n + 1];
						end_index += 2;
					}
					free(ret_coord);
					ret_coord = NULL;
					alter_coord = (int *) realloc(alter_coord,
							(end_index + 2) * sizeof(int));
					if (alter_coord == NULL)
						return NULL;
					alter_coord[end_index] = alter_coord[end_index + 1] = -1;
				}
			}
			start_index = end_index;
		}

		//检查外斜直线坐标
		for (m = 1, player_count = opponent_count = alter_flag = 0; m < 6;
				m++) {
			if (i + m < 5 && j + m < 6) {
				//外斜直线正向未越界
				if (chessboard[i + m][j + m] == player)
					player_count++;
				else if (chessboard[i + m][j + m] == opponent)
					opponent_count++;
			}
			if (i - m >= 0 && j - m >= 0) {
				//外斜直线反向未越界
				if (chessboard[i - m][j - m] == player)
					player_count++;
				else if (chessboard[i - m][j - m] == opponent)
					opponent_count++;
			}
			if (m == 1) {
				if (player_count > 0 || opponent_count == 0)
					break;
				alter_flag = opponent_count;
			} else if (m == 2) {
				if (player_count == 1 && opponent_count == 1)
					alter_flag = 2;
				if (alter_flag != 2) {
					alter_flag = 0;
					break;
				}
			} else if (m > 2) {
				if (player_count > 1 || opponent_count > 2
						|| opponent_count + player_count > 2) {
					alter_flag = 0;
					break;
				}
			}
		}
		if (alter_flag == 2) {
			for (m = 0; m < 6; m++) {
				if (i + m < 5 && j + m < 6) {
					if (chessboard[i + m][j + m] == opponent) {
						if (alter_coord == NULL) {
							alter_coord = (int *) malloc(2 * sizeof(int));
							if (alter_coord == NULL)
								return NULL;
							alter_coord[end_index] = i + m;
							alter_coord[end_index + 1] = j + m;
							end_index += 2;
						} else {
							alter_coord = (int *) realloc(alter_coord,
									(end_index + 2) * sizeof(int));
							if (alter_coord == NULL)
								return NULL;
							alter_coord[end_index] = i + m;
							alter_coord[end_index + 1] = j + m;
							end_index += 2;
						}
					}
				}
				if (i - m >= 0 && j - m >= 0) {
					if (chessboard[i - m][j - m] == opponent) {
						if (alter_coord == NULL) {
							alter_coord = (int *) malloc(2 * sizeof(int));
							if (alter_coord == NULL)
								return NULL;
							alter_coord[end_index] = i - m;
							alter_coord[end_index + 1] = j - m;
							end_index += 2;
						} else {
							alter_coord = (int *) realloc(alter_coord,
									(end_index + 2) * sizeof(int));
							if (alter_coord == NULL)
								return NULL;
							alter_coord[end_index] = i - m;
							alter_coord[end_index + 1] = j - m;
							end_index += 2;
						}
					}
				}
			}
			alter_coord = (int *) realloc(alter_coord,
					(end_index + 2) * sizeof(int));
			if (alter_coord == NULL)
				return NULL;
			alter_coord[end_index] = alter_coord[end_index + 1] = -1;
			for (m = start_index; m < end_index; m += 2) {
				chessboard[alter_coord[m]][alter_coord[m + 1]] = player;
				ret_coord = alter_chess(chessboard, alter_coord[m],
						alter_coord[m + 1]);
				if (ret_coord != NULL) {
					for (n = 0; ret_coord[n] != -1; n += 2) {
						alter_coord = (int *) realloc(alter_coord,
								(end_index + 2) * sizeof(int));
						if (alter_coord == NULL)
							return NULL;
						alter_coord[end_index] = ret_coord[n];
						alter_coord[end_index + 1] = ret_coord[n + 1];
						end_index += 2;
					}
					free(ret_coord);
					ret_coord = NULL;
					alter_coord = (int *) realloc(alter_coord,
							(end_index + 2) * sizeof(int));
					if (alter_coord == NULL)
						return NULL;
					alter_coord[end_index] = alter_coord[end_index + 1] = -1;
				}
			}
		}
	}

	return alter_coord;
}

JNIEXPORT jint JNICALL Java_com_example_jni_ChessJNI_njudge_1fvictory_1nine(
		JNIEnv * env, jobject obj, jobjectArray array) {
	int chessboard[5][7];
	jarray myarray;
	int size = env->GetArrayLength(array);
	for (int i = 0; i < size && i < 5; i++) {
		myarray = (jarray) env->GetObjectArrayElement(array, i);

		int col = env->GetArrayLength(myarray);
		jint *coldata = env->GetIntArrayElements((jintArray) myarray, 0);
		for (int j = 0; j < col && j < 7; j++) {
			chessboard[i][j] = coldata[j];
		}
		env->ReleaseIntArrayElements((jintArray) myarray, coldata, 0);
	}

	return njudge_victroy_nine(chessboard);
}

JNIEXPORT jintArray JNICALL Java_com_example_jni_ChessJNI_nalter_1nchess(
		JNIEnv * env, jobject obj, jobjectArray array, jint m, jint n) {
	int *p;
	int a[10];
	int chessboard[5][7];
	jarray myarray;
	int size = env->GetArrayLength(array);
	jintArray iarr = env->NewIntArray(10);
	for (int i = 0; i < size && i < 5; i++) {
		myarray = (jarray) env->GetObjectArrayElement(array, i);
		int col = env->GetArrayLength(myarray);
		jint *coldata = env->GetIntArrayElements((jintArray) myarray, 0);
		for (int j = 0; j < col && j < 7; j++) {
			chessboard[i][j] = coldata[j];
		}
		env->ReleaseIntArrayElements((jintArray) myarray, coldata, 0);
	}
	for (int j = 0; j < 10; j++)
		a[j] = -1;
	p = alter_chess(chessboard, m, n);
	if (p != NULL) {
		int q = 0;
		for (q = 0; *(p + q) != -1; q++) {
			a[q] = *(p + q);
		}
		free(p);
	}
	env->SetIntArrayRegion(iarr, 0, 10, a);
	return iarr;
}


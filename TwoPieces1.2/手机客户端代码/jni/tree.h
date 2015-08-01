#ifndef _TREE_H_
#define _TREE_H_


typedef struct _TREE_NODE_
{
	int number;
	int player;             //此棋局最后一个下棋者
	int depth;              //当前节点深度
	int weight;             //权重值
	int old_coord[2];
	int new_coord[2];
	int dis_coord[2];
	int chessboard[4][4];
	struct _TREE_NODE_ *parent;
	struct _TREE_NODE_ *child[26];
}TREE_NODE;
extern "C" {
int add_tree_node(TREE_NODE *root, int depth, int chessboard[][4], int old_coord[2], int new_coord[2], int dis_coord[2], int player);
int calculate_weight(TREE_NODE *root);
int pre_traversal_calculate_weight(TREE_NODE *root);
int pruning(TREE_NODE *root, int depth);
int measure_depth(TREE_NODE *root, int *max_depth);
int get_tree_node_amount(TREE_NODE *root, unsigned long int *count);
int judge_victory(int chessboard[][4]);
int get_win_depth(TREE_NODE *root, int player, int *winner_depth);
int destroy_tree(TREE_NODE **root);
}
#endif  //_TREE_H_

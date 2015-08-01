#ifndef _QUEUE_H_
#define _QUEUE_H_

#include "tree.h"

typedef struct _QUEUE_NODE_
{
	TREE_NODE *root;
	struct _QUEUE_NODE_ *next;
}QUEUE_NODE;

typedef struct _QUEUE_INFO_
{
	QUEUE_NODE *head;
	QUEUE_NODE *end;
}QUEUE_INFO;
extern "C" {
int add_queue_node(QUEUE_INFO *queue, TREE_NODE *root);
int peek_queue_node(QUEUE_INFO *queue, TREE_NODE **root);
int fetch_queue_node(QUEUE_INFO *queue, TREE_NODE **root);
int destroy_queue(QUEUE_INFO *queue);
}
#endif  //_QUEUE_H_

#include <stdio.h> 
#include <stdlib.h> 
#include <unistd.h> 
#include <string.h> 
#include <sys/types.h> 
#include <sys/socket.h> 
#include <arpa/inet.h> 
#include <netinet/in.h> 
#include "network.h"
#include "controller.h"
       
#define MAXLINE 1024 
    
/* Khởi tạo socket để nhận dữ liệu */
int startServer(int port) {
    int sockfd; 
    char buffer[MAXLINE]; 
    struct sockaddr_in servaddr, cliaddr; 
    char *response;
        
    // Creating socket file descriptor 
    if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
        perror("socket creation failed"); 
        exit(EXIT_FAILURE); 
    } 
        
    memset(&servaddr, 0, sizeof(servaddr)); 
    memset(&cliaddr, 0, sizeof(cliaddr)); 
        
    // Filling server information 
    servaddr.sin_family    = AF_INET; // IPv4 
    servaddr.sin_addr.s_addr = INADDR_ANY; 
    servaddr.sin_port = htons(port); 
        
    // Bind the socket with the server address 
    if ( bind(sockfd, (const struct sockaddr *)&servaddr, sizeof(servaddr)) < 0 ) 
    { 
        perror("bind failed"); 
        exit(EXIT_FAILURE); 
    } 

    printf("Server started at %d\n", port);
    //const char* response;
    int len, n;
    for (;;) {
        len = sizeof(cliaddr);  //len is value/result
        n = recvfrom(sockfd, (char *)buffer, MAXLINE, MSG_WAITALL, ( struct sockaddr *) &cliaddr, &len); 
        buffer[n] = '\0';
        response = handlerRequest(buffer, cliaddr);
        sendto(sockfd, response, strlen(response), MSG_CONFIRM, (const struct sockaddr *) &cliaddr, len);
        //printf("Hello message sent.\n"); 
    }
     
        
    return 0; 
}
# Discord Chat Bot for Maplestory (Kalpago)
Discord bot that synchronizes its text channel with a chat from an online game Maplestory (KMS). Provides support for various commands that is used for alarms and member access control.

The program must contain 0.png, n.png, m.png, and fonttree.txt files in its root to run. Please update your bot token and text channel ids in Main class.

Class interactions (Major ones and helper classes):

Main -- [GUI, jdaListener]  
|  
gameInteraction -- [jscomm, scheduler, Actions, discordUtilities]  
|  
gameScreenAudit   
|  
fontTree, capture

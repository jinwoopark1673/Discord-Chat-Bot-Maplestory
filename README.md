# Discord Chat Bot for Maplestory (Kalpago)
Discord bot that synchronizes its text channel with a chat from an online game Maplestory (KMS). Provides support for various commands that is used for alarms and member access control.

Please update your bot token and text channel ids in Main class. resource folder must be added in resource root.

Class interactions (Major ones and helper classes):

Main -- [GUI, jdaListener]  
|  
gameInteraction -- [jscomm, scheduler, Actions, discordUtilities]  
|  
gameScreenAudit   
|  
fontTree, capture

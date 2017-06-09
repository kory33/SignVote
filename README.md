[![Build Status](https://travis-ci.org/kory33/SignVote.svg?branch=master)](https://travis-ci.org/kory33/SignVote)

### What is this?
SignVote is a Bukkit plug-in which allows you to set-up voting sessions.

### How to use
Before you can actually start voting, you need to set-up a session.

To create a session, simply run `/signvote create <session name>` command
(it may not be recommended to use any character other than alphanumeric as a session name.)

Then you should add a score-limit(the number of times a player can
vote with the specified score) to the session you've just created.
To do so, run `/signvote addscore <session name>`,
put desired values in the shown interface and submit it.

Once you have set all the score-limits to the session, you need to create "vote-points".
Vote-points are the points to which players will vote. To create a vote-point,
put a signboard and then enter texts in the following format:

```
[SignVote]
<session name>
<vote-point name>
 
```
(leave the last line blank)

Now everything is ready!
You can ask the players to click on the sign and then vote!

### Permissions
All the permissions used by this plugin starts with `signvote` node.
Here's the list of permissions:
 - `signvote.vote` : allows a player to vote
 - `signvote.votemore` : default permission node for `op` permission specifier in `addscore` command
 - `signvote.unvote`: allows a player to cancel a vote
 - `signvote.createsign`: allows a player to create a new vote point
 - `signvote.createsession`: allows a player to create a vote session
 - `signvote.modifysession`: allows a player to modify the existing session
 - `signvote.listsession`: allows a player to obtain the session list
 - `signvote.opensession`: allows a player to re-open a closed session
 - `signvote.closesession`: allows a player to close an opened session
 - `signvote.deletesession`: allows a player to delete an existing session
 - `signvote.deletevotepoint`: allows a player to delete a vote point
 - `signvote.viewstats`: allows a player to view the statistics of a vote session
 - `signvote.reload`: allows a player to reload the plugin
 - `signvote.save`: allows a player to save plugin data

All permissions except `signvote.vote` and `signvote.unvote` are only enabled for OPs by default.

### Downloads
You can obtain a copy of build from the [release](https://github.com/kory33/SignVote/releases) page.

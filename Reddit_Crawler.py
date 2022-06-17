#!/usr/bin/python3

import praw
from praw.models import MoreComments
import pandas as pd
import os

subreddits = [
"electricvehicles",
"PowerShell",
"scriptwriting",
"Monitors",
"Smartphones",
"iphone",
"samsung",
"ipad",
"hardwareswap",
"csMajors"
]
#    "programming"
#     "askprogramming",
#     "learnprogramming",
#     "computerscience",
#     "compsci"
#     "askcomputerscience",
#     "coding",
#     "algorithms",
#     "software architecture"
#     "functionalprogramming",
#     "progarmminglanguages",
#     "graphicsprogramming",
#     "webdev",
#     "web_design",
#     "iosprogramming"
#     "c_programming",
#     "cprogramming",
#     "c_language",
#     "cpp",
#     "cplusplus",
#     "cpp_questions",
#     "python",
#     "pythonhelp",
#     "learnpython",
#     "java",
#     "javahelp",
#     "learnjava",
#     "rust",
#     "javascript",
#     "learnjavascript",
#     "javscripthelp",
#     "golang",
#     "haskell",
#     "csharp",
#     "scala",
#     "clojure",
#     "lisp",
#     "swift",
#     "typescript"
#     "html5",
#     "css"
#     "cuda",
#     "opengl",
#     "vulkan",
#     "simd",
#     "gpgpu",
#     "node",
#     "rails",
#     "django",
#     "angularjs",
#     "vuejs",
#     "dotnet",
#     "flask"
#     "ruby",
#     "unix",
#     "osdev",
#     "SQL",
#     "learnSQL",
#     "SQLServer",
#     "Database",
#     "datascience",
#     "sysadmin",
#     "AZURE",
#     "netsec",
#     "software",
#     "cscareerquestions",
#     "technology",
#     "opensource",
#     "techsupport",
#     "computervision",
#     "intel",
#     "nvidia",
#     "amd",
#     "hardware",
#     "oculus",
#     "linux",
#     "ios",
#     "android",
#     "ubuntu",
#     "cryptocurrency",
#     "windows",
#     "MachineLearning",
#     "reactjs",
#     "Frontend",
#     "PHP",
#     "WordPress",
#     "ProWordPress",
#     "CodingHelp",
#     "node",
#     "devops",
#     "datamining",
#     "softwaredevelopment",
#     "code",
#     "matlab"
# ]

columns = [
    "type",
    "score",
    "id",
    "title",
    "text",
    "subreddit",
    "timestamp"
]

target_file_size = 536870912

def main():
    reddit = praw.Reddit(
        client_id = "p7kjKm8DDsoU7k5OVQ5w6g",
        client_secret = "Da5hZTiCMv8niTVEDOlWld93sYOcOg",
        user_agent = "macos:crawler:v1.0.0 (by u/donotcry_9527)"
    )

    for subreddit in subreddits:
        print("Scraping: ", subreddit)
        entries = []

        posts = reddit.subreddit(subreddit).new(limit=None)
        i = 0
        j = 0
        for post in posts:
            print("Post #", i)
            i += 1
            #TODO: Add entry for post
            entries.append([
                "post",
                post.score,
                post.id,
                post.title,
                post.selftext,
                subreddit,
                post.created
            ])

            comments = post.comments
            comments.replace_more(limit=None)
            comment_queue = comments[:]  # Seed with top-level
            while comment_queue:
                comment = comment_queue.pop(0)
                print("Comment #", j)
                j += 1
                entries.append([
                    "comment",
                    comment.score,
                    comment.id,
                    "",
                    comment.body,
                    subreddit,
                    comment.created
                ])
                comment_queue.extend(comment.replies)

            df = pd.DataFrame(entries,columns=columns)
            df.to_csv(subreddit + ".csv")

        df = pd.DataFrame(entries,columns=columns)
        df.to_csv(subreddit + ".csv")
        print("Finished: ", subreddit)

if __name__ == "__main__":
    main()

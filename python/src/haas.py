"""
Created on Nov 17, 2010

@author: timothy
"""
import sys
from haas_logger import getLogger
from haas_console import ConsoleView
from haas_guitk import GUITkView
from haas_engine import Engine


def main():
    try:
        getLogger().log("Logging is enabled")
        getLogger().log("Command Line Arguments: ", sys.argv)

        if "--console" in sys.argv:
            getLogger().log("Running in Console view")
            view = ConsoleView()
            view.run(Engine(view))
        elif "--server" in sys.argv:
            getLogger().log("Running in Server mode")
            engine = Engine()
            engine.launch()
        else:
            getLogger().log("Running in GUITk view")
            view = GUITkView()
            view.run(Engine(view))

    finally:
        getLogger().log("Game exited...shutting down game")


if __name__ == '__main__':
    main()

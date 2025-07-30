# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

The goal of this project is to build a simple voting system for a parental voting system for a school. The voting system should provide a voting tool for voting for a parent parent spokesperson in a class. Each parent has exactly one vote. The teacher/admin should be able to add each parent in a simple webform. No authentication is required. The teacher can then start the voting. In a second step the teacher can mark the parents who can be voted. Then voting can be started. Then the system shows a voting screen for each parent. Then the parent can vote for ONE person from the set of votable persons. Then the voting screen for the next parent is shown. This process should be repeated until all parents have voted. The teacher should have the possibility to skip a voting person, for example if the parent is absent.
The system should persist the votes and after the voting show a result screen with the voted spokesperson (showing the number of recieved votes), and the spokesperson deputy (the person with the second large amount of votes).

## Development Setup

- Rich client using modern java 

## Project Structure

- Best practices for java rich client application

## Key Considerations

When developing this voting system:
- Ensure vote integrity and prevent duplicate voting

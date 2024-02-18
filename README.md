# Wiki Search

Wikipedia search powered by Apache Lucene. Written and video presentations can
be found in [docs](./docs).

## Team Members

- Andrei Lupșa
- Mădălin Raiu
- Tudor Lechințan

## Running the Project

To compile and run the project use `mvn compile exec:java` in the root of the
project.

## Generating the Index

The index can be generated using the `index` command. The command reads the
files from the `lucene_input` directory, splits each file into documents which
are then written to `lucene_documents` and generates an index in the
`lucene_output` directory.

## Answering the Questions

Questions from the [questions.txt](./questions.txt) file can be answered using
the `answer` command. For each question, the command outputs the question text,
the answer retrieved from Lucene and the correct answer. At the end, the number
of answers that match is displayed.

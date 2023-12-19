export const mockData = {
  result: "success",
  headers: [
    {
      title: "Subjects",
      question: "Who were the subjects of the study?",
      keywordList: ["sheep", "children", "mice"],
    },
    
  ],
  fileList: [
    {
      result: "success",
      filepath: "data/LargerTest/Asthma.pdf",
      title: "Asthma",
      metadata: [
        {
          result: "success",
          rawResponse:
            "The study included 127 children with asthma who were diagnosed and treated at the Private Practice Cebe-lica in Maribor, Slovenia, over a period of 12 consecutive months.",
          data: {
            children: [0.7700431458051551, 0.0028405986914380147],
            mice: [0.19251078645128877, 0.0],
            sheep: [0.19251078645128877, 0.0],
          },
        },
      ],
    },
    {
      result: "success",
      filepath: "data/LargerTest/Biopsy.pdf",
      title: "Biopsy",
      metadata: [
        {
          result: "success",
          rawResponse:
            "The study included 121 patients with peripheral lung infectious lesions.",
          data: {
            children: [0.2283793118709102, 0.0],
            mice: [0.2283793118709102, 0.0],
            sheep: [0.2283793118709102, 0.0],
          },
        },
      ],
    },
  ],
};

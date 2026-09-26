import pandas as pd

# File names
input_file = "personal_finance_tracker_dataset.csv"
test_file = "test.csv"

# Read the dataset
data = pd.read_csv(input_file)

# Shuffle the dataset
data = data.sample(frac=1, random_state=42).reset_index(drop=True)

# Take 20% of the rows
test_size = int(len(data) * 0.20)

test_data = data.iloc[:test_size]
train_data = data.iloc[test_size:]

# Save the 20% to a new file
test_data.to_csv(test_file, index=False)

# Save the remaining 80% back to the original file
train_data.to_csv(input_file, index=False)

print(f"Original rows: {len(data)}")
print(f"Training rows: {len(train_data)}")
print(f"Test rows: {len(test_data)}")
print("Dataset successfully split.")
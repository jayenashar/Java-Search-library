package org.jayenashar.search.examples.shoppingplan;

import au.edu.unsw.cse.jayen.bisearch.AStarSearch;
import au.edu.unsw.cse.jayen.bisearch.StateSpaceSearchProblem;
import au.edu.unsw.cse.jayen.search.Action;
import au.edu.unsw.cse.jayen.search.ActionStatePair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * https://code.google.com/codejam/contest/32003/dashboard#s=p3
 * <p>
 * Created by jayen on 23/07/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class ShoppingPlan {
    /**
     * @param args one input filename
     */
    public static void main(String[] args) throws IOException {
        final String filename = args[0];
        final BufferedReader reader = new BufferedReader(new FileReader(filename));
        final byte numCases = Byte.parseByte(reader.readLine());
        for (byte caseIndex = 1; caseIndex <= numCases; caseIndex++) {
            final double minimumSpend = new Case(reader).minimumSpend();
            System.out.println("Case #" + caseIndex + ": " + String.format("%.7f", minimumSpend));
        }
    }

    private static class Item {
        private final String  name;
        private final boolean perishable;

        private Item(final String s) {
            this(s.replaceFirst("!$", ""), s.endsWith("!"));
        }

        private Item(final String name, final boolean perishable) {
            this.name = name;
            this.perishable = perishable;
        }
    }

    private static class Store {
        private final short      xPos;
        private final short      yPos;
        private final List<Item> items;

        private Store(final String s) {
            final String[] store = s.split(" ", 3);
            xPos = Short.parseShort(store[0]);
            yPos = Short.parseShort(store[1]);
            items = Arrays.stream(store[2].split(" ")).map(Item::new).collect(Collectors.toList());
        }

        private static class Item {
            private final String name;
            private final short  price;

            private Item(final String s) {
                this(s.split(":"));
            }

            private Item(final String[] strings) {
                this(strings[0], Short.parseShort(strings[1]));
            }

            private Item(final String name, final short price) {
                this.name = name;
                this.price = price;
            }

            private boolean isPerishable(final List<ShoppingPlan.Item> items) {
                return items.stream().anyMatch(item -> item.perishable && item.name.equals(name));
            }
        }
    }

    private static class Case {
        private final short       priceOfGas;
        private final List<Item>  items;
        private final List<Store> stores;

        private Case(final BufferedReader reader) throws IOException {
            final String[] itemsStoresGas = reader.readLine().split(" ");
            final byte numItems = Byte.parseByte(itemsStoresGas[0]);
            final byte numStores = Byte.parseByte(itemsStoresGas[1]);
            priceOfGas = Short.parseShort(itemsStoresGas[2]);

            items = Arrays.stream(reader.readLine().split(" ")).map(Item::new).collect(Collectors.toList());
            assert numItems == items.size();

            stores = new ArrayList<>(numStores);
            for (int storeIndex = 0; storeIndex < numStores; storeIndex++)
                stores.add(new Store(reader.readLine()));
        }

        private double minimumSpend() {
            return new AStarSearch<State>(state -> {
                final double itemsMinPrice = state.itemsToBuy.stream().mapToDouble(this::getMinPrice).sum();
                final double goHomePrice = Math.hypot(state.xPos, state.yPos) * priceOfGas;
                return itemsMinPrice + goHomePrice;
            }, state -> {
                final double itemsMinPrice = state.itemsBought.stream().mapToDouble(this::getMinPrice).sum();
                final double goHomePrice = Math.hypot(state.xPos, state.yPos) * priceOfGas;
                return itemsMinPrice + goHomePrice;
            }).search(new StateSpaceSearchProblem<State>() {
                @Override
                public Iterable<State> goalStates() {
                    return Collections.singleton(new State(Collections.emptyList(), items, (short) 0, (short) 0, 0));
                }

                @Override
                public Iterable<State> initialStates() {
                    return Collections.singleton(new State(items, Collections.emptyList(), (short) 0, (short) 0, 0));
                }

                @Override
                public Iterable<ActionStatePair<State>> predecessor(final State state) {
                    return null;
                }

                @Override
                public Iterable<ActionStatePair<State>> successor(final State state) {
                    return stores.stream().map(store -> {
                        final List<Store.Item> itemsMayBy = store.items.stream().filter(storeItem -> isToBuy(state, storeItem))
                                                                       .collect(Collectors.toList());
                        final Stream<List<Store.Item>> allCombinationsOfItems =
                                StreamSupport.stream(((Iterable<List<Store.Item>>) () -> new Iterator<List<Store.Item>>() {
                                    private long combinationBitFlag = 1;

                                    @Override
                                    public boolean hasNext() {
                                        return combinationBitFlag < 1 << itemsMayBy.size();
                                    }

                                    @Override
                                    public List<Store.Item> next() {
                                        long combinationBitFlag2 = combinationBitFlag;
                                        int itemIndex = 0;
                                        final List<Store.Item> itemsToBuy = new ArrayList<>(itemsMayBy.size());
                                        do {
                                            if ((combinationBitFlag2 & 1) == 1)
                                                itemsToBuy.add(itemsMayBy.get(itemIndex));
                                            combinationBitFlag2 >>= 1;
                                        } while (combinationBitFlag2 != 0);
                                        ++combinationBitFlag;
                                        return itemsToBuy;
                                    }
                                }).spliterator(), false);
                        return allCombinationsOfItems.map(storeItemsToBuy -> {
                            final boolean isBuyingPerishable = storeItemsToBuy.stream().anyMatch(storeItem -> storeItem.isPerishable(items));
                            final Action action = () -> {
                                final double itemsPrice = storeItemsToBuy.stream().mapToDouble(item -> item.price).sum();
                                final double priceOfGas1 = Math.hypot(state.xPos - store.xPos, state.yPos - store.yPos) *
                                                           Case.this.priceOfGas;
                                if (isBuyingPerishable)
                                    // go home
                                    return itemsPrice + priceOfGas1 + Math.hypot(store.xPos, store.yPos) * Case.this.priceOfGas;
                                else
                                    // stay at the store
                                    return itemsPrice + priceOfGas1;
                            };
                            final List<Item> itemsToBuy = storeItemsToBuy.stream().map(storeItem -> Case.this.items.stream()
                                                                                                                    .filter(item -> item.name
                                                                                                                            .equals(storeItem.name))
                                                                                                                    .findAny()
                                                                                                                    .get()).collect(
                                    Collectors.toList());
                            final List<Item> itemsBought = new ArrayList<>(state.itemsBought.size() + itemsToBuy.size());
                            itemsBought.addAll(state.itemsBought);
                            itemsBought.addAll(itemsToBuy);
                            final List<Item> itemsToBuy2 = new ArrayList<>(state.itemsToBuy.size() - itemsToBuy.size());
                            itemsToBuy2.addAll(state.itemsToBuy);
                            itemsToBuy2.removeAll(itemsBought);
                            final State state2 = new State(itemsBought, itemsToBuy2, isBuyingPerishable ? 0 : store.xPos,
                                                           isBuyingPerishable ? 0 : store.yPos, state.spend + action.cost());
                            return new ActionStatePair<>(action, state2);
                        });
                    }).flatMap(Function.identity()).collect(Collectors.toList());
                }
            }).stream().mapToDouble(Action::cost).sum();
        }

        private static boolean isToBuy(final State state, final Store.Item storeItem) {
            return state.itemsToBuy.stream().anyMatch(item -> item.name.equals(storeItem.name));
        }

        private double getMinPrice(final Item item) {
            return stores.stream().mapToDouble(store -> getPrice(item, store)).min().getAsDouble();
        }

        private static double getPrice(final Item item, final Store store) {
            return store.items.stream().filter(storeItem -> storeItem.name.equals(item.name))
                              .mapToDouble(storeItem -> storeItem.price).min().orElse(Double.POSITIVE_INFINITY);
        }

        private class State {
            private final List<Item> itemsBought;
            private final List<Item> itemsToBuy;
            private final short      xPos;
            private final short      yPos;
            private final double     spend;

            private State(final List<Item> itemsBought,
                          final List<Item> itemsToBuy,
                          final short xPos,
                          final short yPos,
                          final double spend) {
                this.itemsBought = itemsBought;
                this.itemsToBuy = itemsToBuy;
                this.xPos = xPos;
                this.yPos = yPos;
                this.spend = spend;

                itemsBought.sort((item1, item2) -> item1.name.compareTo(item2.name));
                itemsToBuy.sort((item1, item2) -> item1.name.compareTo(item2.name));
            }

            @Override
            public int hashCode() {
                return Objects.hash(itemsBought, itemsToBuy, xPos, yPos);
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                final State state = (State) o;
                return xPos == state.xPos &&
                       yPos == state.yPos &&
                       Objects.equals(itemsBought.size(), state.itemsBought.size()) &&
                       Objects.equals(itemsToBuy.size(), state.itemsToBuy.size());
            }
        }
    }
}
